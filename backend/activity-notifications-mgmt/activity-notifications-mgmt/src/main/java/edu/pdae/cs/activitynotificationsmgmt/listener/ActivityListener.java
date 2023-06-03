package edu.pdae.cs.activitynotificationsmgmt.listener;

import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
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

        if (activityFiredDTO.getMemoId() == null) {
            activityService.addActivity(activityFiredDTO.getUser(), activityFiredDTO.getUserName(), new ObjectId(activityFiredDTO.getHubId()), activityFiredDTO.getHubName(), activityFiredDTO.getDate(), activityFiredDTO.getType(), activityFiredDTO.getVisibility());
        } else {
            activityService.addActivity(activityFiredDTO.getUser(), activityFiredDTO.getUserName(), new ObjectId(activityFiredDTO.getHubId()), activityFiredDTO.getHubName(), new ObjectId(activityFiredDTO.getMemoId()), activityFiredDTO.getMemoTitle(), activityFiredDTO.getDate(), activityFiredDTO.getType(), activityFiredDTO.getVisibility());
        }

        if (activityFiredDTO.getVisibility().equals(Visibility.PUBLIC)) {
            notificationDTOKafkaTemplate.send(MessagingConfiguration.NOTIFICATIONS_TOPIC, NotificationMessageDTO.builder()
                    .user(activityFiredDTO.getUser())
                    .userName(activityFiredDTO.getUserName())
                    .hubId(activityFiredDTO.getHubId())
                    .hubName(activityFiredDTO.getHubName())
                    .memoId(activityFiredDTO.getMemoId())
                    .memoTitle(activityFiredDTO.getMemoTitle())
                    .type(activityFiredDTO.getType())
                    .build());
        }
    }

}
