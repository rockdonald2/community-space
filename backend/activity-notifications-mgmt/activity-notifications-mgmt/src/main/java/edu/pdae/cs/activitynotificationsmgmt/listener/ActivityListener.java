package edu.pdae.cs.activitynotificationsmgmt.listener;

import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.NotificationMessageDTO;
import edu.pdae.cs.activitynotificationsmgmt.service.ActivityService;
import edu.pdae.cs.activitynotificationsmgmt.service.MemoService;
import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.model.dto.ActivityFiredDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ActivityListener {

    private final ActivityService activityService;
    private final KafkaTemplate<String, NotificationMessageDTO> notificationDTOKafkaTemplate;
    private final MemoService memoService;

    @KafkaListener(topics = MessagingConfiguration.ACTIVITY_TOPIC,
            groupId = "cs-activity-mgmt:activity-listener-group",
            autoStartup = "true",
            containerFactory = "activityDTOConcurrentKafkaListenerContainerFactory")
    public void activityListener(@Payload ActivityFiredDTO activityFiredDTO) {
        log.info("Caught internal message for activity update: {}", activityFiredDTO);

        activityService.addActivity(activityFiredDTO);

        if (activityFiredDTO.getActivityType().equals(Type.MEMO_COMPLETED)) {
            memoService.addCompletion(new ObjectId(activityFiredDTO.getMemoId()), activityFiredDTO.getTakerUser().getEmail());
        }

        if (activityFiredDTO.getActivityVisibility().equals(Visibility.PUBLIC)) {
            notificationDTOKafkaTemplate.send(MessagingConfiguration.NOTIFICATIONS_TOPIC, NotificationMessageDTO.builder()
                    .takerUser(activityFiredDTO.getTakerUser())
                    .affectedUsers(activityFiredDTO.getAffectedUsers())
                    .hubId(activityFiredDTO.getHubId())
                    .hubName(activityFiredDTO.getHubName())
                    .memoId(activityFiredDTO.getMemoId())
                    .memoTitle(activityFiredDTO.getMemoTitle())
                    .type(activityFiredDTO.getActivityType())
                    .build());
        }
    }

}
