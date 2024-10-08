package edu.pdae.cs.activitynotificationsmgmt.service.impl;

import com.corundumstudio.socketio.SocketIOServer;
import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
import edu.pdae.cs.activitynotificationsmgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.activitynotificationsmgmt.listener.NotificationListener;
import edu.pdae.cs.activitynotificationsmgmt.model.Hub;
import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import edu.pdae.cs.activitynotificationsmgmt.model.Notification;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.DueMemoDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.NotificationMessageDTO;
import edu.pdae.cs.activitynotificationsmgmt.repository.NotificationRepository;
import edu.pdae.cs.activitynotificationsmgmt.service.HubService;
import edu.pdae.cs.activitynotificationsmgmt.service.MemoService;
import edu.pdae.cs.activitynotificationsmgmt.service.NotificationService;
import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.model.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final int HARD_RETURN_LIMIT = 15;

    private final NotificationRepository notificationRepository;
    private final MemoService memoService;
    private final HubService hubService;
    private final ModelMapper modelMapper;
    private final SocketIOServer socketIOServer;
    private final RedisTemplate<String, Date> lastSentRedisTemplate;
    private final KafkaTemplate<String, DueMemoDTO> dueMemoDTOKafkaTemplate;

    @Value("${cs.notification-reminders.timeout.minutes}")
    private int reminderTimeoutMins;

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    public void readNotification(ObjectId notificationId, String user) throws NoSuchElementException, ForbiddenOperationException {
        final Notification notification = notificationRepository.findById(notificationId).orElseThrow();

        if (!notification.getOwner().equals(user) && !notification.getOwner().equals(Notification.GroupTargets.GENERAL.getValue())) {
            throw new ForbiddenOperationException("You can't read this notification");
        }

        notification.getReads().add(user);
        notificationRepository.save(notification);
    }

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    public Notification addNotification(String target, Notification.TargetType targetType, String msg, String takerEmail) {
        return notificationRepository.save(Notification.builder()
                .owner(target)
                .msg(msg)
                .reads(new HashSet<>())
                .createdAt(new Date())
                .targetType(targetType)
                .taker(takerEmail)
                .build());
    }

    @Scheduled(fixedDelayString = "${cs.notification-reminders.timeout.minutes}", timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "handleDueMemos", lockAtMostFor = "#{${cs.notification-reminders.timeout.minutes} - 1}m", lockAtLeastFor = "#{${cs.notification-reminders.timeout.minutes} - 1}m")
    @Override
    public void queryDueMemos() {
        log.info("Checking due memos and broadcasting notifications");

        final Optional<Date> lastSent = Optional.ofNullable(lastSentRedisTemplate.opsForValue().get("cs:activity-notifications-mgmt:due-memos"));

        if (lastSent.isPresent()) {
            final var diffHours = Duration.between(lastSent.get().toInstant(), Instant.now()).toMinutes();

            if (diffHours < (reminderTimeoutMins - 1)) {
                log.info("Skipping due memos check, last check was {} minutes ago", diffHours);
                return;
            }
        }

        memoService.getDueMemos().forEach(memo -> dueMemoDTOKafkaTemplate.send(MessagingConfiguration.DUE_MEMO_TOPIC, DueMemoDTO.builder().memoId(memo.getId().toHexString()).build()));

        // it's good at the bottom, because if the above code fails, we don't want to set the last sent time
        // anyway we use locking
        lastSentRedisTemplate.opsForValue().set("cs:activity-notifications-mgmt:due-memos", new Date());
    }

    @Override
    public void handleDueMemo(DueMemoDTO dueMemoDTO) {
        final Memo memo = memoService.getMemo(new ObjectId(dueMemoDTO.getMemoId()));

        if (memo.isArchived()) {
            log.warn("Due memo became archived before it was handled, skipping");
            return;
        }

        final Hub hub = hubService.getHub(memo.getHubId());

        // get all members who haven't completed yet
        final Set<String> completions = memo.getCompletions();
        final Set<String> members = hub.getMembers();
        members.add(hub.getOwner()); // add owner to the members list

        members.removeAll(completions); // remove all who've already completed this memo
        members.remove(memo.getOwner()); // remove the owner (who created this memo)

        final Instant due = memo.getDueDate().toInstant();
        final Duration diff = Duration.between(Instant.now(), due);

        String diffString;
        if (diff.toHours() > 0) {
            diffString = String.format("%s hours and %s minutes", diff.toHours(), diff.toMinutesPart());
        } else {
            diffString = String.format("%s minutes", diff.toMinutesPart());
        }

        // notify everyone, except the action taker
        members.forEach(member -> {
            final var notification = addNotification(member, Notification.TargetType.USER, String.format("You have a memo %s in %s due in %s", memo.getTitle(), hub.getName(), diffString), memo.getOwner());
            broadcastNotification(member, NotificationDTO.builder()
                    .id(notification.getId().toHexString())
                    .msg(String.format("You have a memo %s in %s due in %s", memo.getTitle(), hub.getName(), diffString))
                    .createdAt(notification.getCreatedAt())
                    .taker(memo.getOwner())
                    .build());
        });
    }

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    public void handleNotification(NotificationMessageDTO notificationMessageDTO) {
        // if HUB_CREATED => general
        // if MEMO_COMPLETED => owner
        // if MEMO_CREATED => all members of the hub

        final var notificationType = notificationMessageDTO.getType();

        if (Type.HUB_CREATED.equals(notificationType)) {
            final var notification = addNotification(Notification.GroupTargets.GENERAL.getValue(), Notification.TargetType.GROUP, String.format("New hub created %s by %s", notificationMessageDTO.getHubName(), notificationMessageDTO.getTakerUser().getName()), notificationMessageDTO.getTakerUser().getEmail());

            readNotification(notification.getId(), notificationMessageDTO.getTakerUser().getEmail());
            broadcastNotification(Notification.GroupTargets.GENERAL.getValue(), modelMapper.map(notification, NotificationDTO.class));
        } else if (Type.MEMO_COMPLETED.equals(notificationType)) {
            Objects.requireNonNull(notificationMessageDTO.getMemoId());
            final Memo memo = memoService.getMemo(new ObjectId(notificationMessageDTO.getMemoId()));
            final var notificationMessage = String.format("%s has completed your memo %s in %s", notificationMessageDTO.getTakerUser().getName(), memo.getTitle(), notificationMessageDTO.getHubName());

            final var notification = addNotification(memo.getOwner(), Notification.TargetType.USER, notificationMessage, notificationMessageDTO.getTakerUser().getEmail());
            broadcastNotification(memo.getOwner(), NotificationDTO.builder()
                    .id(notification.getId().toHexString())
                    .msg(notificationMessage)
                    .createdAt(notification.getCreatedAt())
                    .taker(notificationMessageDTO.getTakerUser().getEmail())
                    .build());
        } else if (Type.MEMO_CREATED.equals(notificationType)) {
            Objects.requireNonNull(notificationMessageDTO.getHubId());
            final var hub = hubService.getHub(new ObjectId(notificationMessageDTO.getHubId()));
            final var notificationMessage = String.format("%s has created a new memo in your hub %s", notificationMessageDTO.getTakerUser().getName(), hub.getName());

            // notify everyone, except the action taker
            hub.getMembers().stream().filter(member -> !member.equals(notificationMessageDTO.getTakerUser().getEmail())).forEach(member -> {
                final var notification = addNotification(member, Notification.TargetType.USER, notificationMessage, notificationMessageDTO.getTakerUser().getEmail());
                broadcastNotification(member, NotificationDTO.builder()
                        .id(notification.getId().toHexString())
                        .msg(notificationMessage)
                        .createdAt(notification.getCreatedAt())
                        .taker(notificationMessageDTO.getTakerUser().getEmail())
                        .build());
            });

            if (!notificationMessageDTO.getTakerUser().getEmail().equals(hub.getOwner())) {
                final var notification = addNotification(hub.getOwner(), Notification.TargetType.USER, String.format("%s has created a new memo in your hub %s", notificationMessageDTO.getTakerUser().getName(), hub.getName()), notificationMessageDTO.getTakerUser().getEmail());
                broadcastNotification(hub.getOwner(), NotificationDTO.builder()
                        .id(notification.getId().toHexString())
                        .msg(notificationMessage)
                        .createdAt(notification.getCreatedAt())
                        .taker(notificationMessageDTO.getTakerUser().getEmail())
                        .build());
            }
        } else if (Type.MEMBER_JOINED.equals(notificationType) || Type.MEMBER_REMOVED.equals(notificationType)) {
            Objects.requireNonNull(notificationMessageDTO.getHubId());
            final var hub = hubService.getHub(new ObjectId(notificationMessageDTO.getHubId()));
            final var notificationMessage = String.format("You have been %s hub %s", Type.MEMBER_JOINED.equals(notificationType) ? "accepted to" : "removed from", hub.getName());
            final var affectedUser = notificationMessageDTO.getAffectedUsers().stream().findFirst().orElseThrow();

            final var notification = addNotification(affectedUser.getEmail(), Notification.TargetType.USER, notificationMessage, notificationMessageDTO.getTakerUser().getEmail());
            broadcastNotification(affectedUser.getEmail(), NotificationDTO.builder()
                    .id(notification.getId().toHexString())
                    .msg(notificationMessage)
                    .createdAt(notification.getCreatedAt())
                    .taker(notificationMessageDTO.getTakerUser().getEmail())
                    .build());
        }
    }

    @Override
    public void broadcastNotification(String target, NotificationDTO notificationDTO) {
        log.info("Broadcasting notification {} to target {}", notificationDTO, target);
        socketIOServer.getRoomOperations(target).sendEvent(NotificationListener.Events.NOTIFICATION.getValue(), notificationDTO);
    }

    @Override
    @Cacheable("notifications")
    public List<NotificationDTO> getNotifications(String target, String asUser, Date from, Date to) {
        return notificationRepository.findAllByOwnerAndReadsNotContainsAndCreatedAtBetweenOrderByCreatedAtDesc(target, asUser, from, to, Pageable.ofSize(HARD_RETURN_LIMIT)).getContent()
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .toList();
    }

}
