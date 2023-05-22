package edu.pdae.cs.activitynotificationsmgmt.listener;

import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
import edu.pdae.cs.activitynotificationsmgmt.model.Activity;
import edu.pdae.cs.activitynotificationsmgmt.service.ActivityService;
import edu.pdae.cs.common.model.dto.ActivityFiredDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ActivityListener {

    private final ActivityService activityService;

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
        } else {
            log.error("Unknown activity type: {}", activityFiredDTO.getType());
            return;
        }

        activityService.addActivity(new ObjectId(activityFiredDTO.getHubId()), activityFiredDTO.getDate(), type);
    }

}
