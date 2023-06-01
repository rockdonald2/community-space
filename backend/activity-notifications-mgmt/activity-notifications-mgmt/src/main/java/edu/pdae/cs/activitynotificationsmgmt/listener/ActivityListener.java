package edu.pdae.cs.activitynotificationsmgmt.listener;

import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
import edu.pdae.cs.activitynotificationsmgmt.model.Activity;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.NotificationMessageDTO;
import edu.pdae.cs.activitynotificationsmgmt.service.ActivityService;
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

    @KafkaListener(topics = MessagingConfiguration.ACTIVITY_TOPIC,
            groupId = "cs-activity-mgmt:activity-listener-group",
            autoStartup = "true",
            containerFactory = "activityDTOConcurrentKafkaListenerContainerFactory")
    public void activityListener(@Payload ActivityFiredDTO activityFiredDTO) {
        log.info("Caught internal message for activity update: {}", activityFiredDTO);

        Activity.Type type;
        if (activityFiredDTO.getType().equals(ActivityFiredDTO.Type.MEMO_CREATED)) {
            type = Activity.Type.MEMO_CREATED;
        } else if (activityFiredDTO.getType().equals(ActivityFiredDTO.Type.HUB_CREATED)) {
            type = Activity.Type.HUB_CREATED;
        } else if (activityFiredDTO.getType().equals(ActivityFiredDTO.Type.MEMO_COMPLETED)) {
            type = Activity.Type.MEMO_COMPLETED;
        } else {
            log.error("Unknown activity type: {}", activityFiredDTO.getType());
            return;
        }

        if (activityFiredDTO.getMemoId() == null) {
            activityService.addActivity(activityFiredDTO.getUser(), new ObjectId(activityFiredDTO.getHubId()), activityFiredDTO.getHubName(), activityFiredDTO.getDate(), type, activityFiredDTO.getVisibility());
        } else {
            activityService.addActivity(activityFiredDTO.getUser(), new ObjectId(activityFiredDTO.getHubId()), activityFiredDTO.getHubName(), new ObjectId(activityFiredDTO.getMemoId()), activityFiredDTO.getMemoTitle(), activityFiredDTO.getDate(), type, activityFiredDTO.getVisibility());
        }

        if (activityFiredDTO.getVisibility().equals(Visibility.PUBLIC)) {
            notificationDTOKafkaTemplate.send(MessagingConfiguration.NOTIFICATIONS_TOPIC, NotificationMessageDTO.builder()
                    .user(activityFiredDTO.getUser())
                    .hubId(activityFiredDTO.getHubId())
                    .hubName(activityFiredDTO.getHubName())
                    .memoId(activityFiredDTO.getMemoId())
                    .memoTitle(activityFiredDTO.getMemoTitle())
                    .type(NotificationMessageDTO.Type.valueOf(activityFiredDTO.getType().name()))
                    .build());
        }
    }

}
