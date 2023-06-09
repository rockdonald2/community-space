package edu.pdae.cs.activitynotificationsmgmt.listener;

import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
import edu.pdae.cs.activitynotificationsmgmt.service.HubService;
import edu.pdae.cs.common.model.dto.HubMemberMutationDTO;
import edu.pdae.cs.common.model.dto.HubMutationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HubChangesListener {

    private final HubService hubService;

    @KafkaListener(topics = MessagingConfiguration.MEMBER_MUTATION_TOPIC,
            groupId = "cs-activity-notifications-mgmt:hub-member-mutation-listener-group",
            autoStartup = "true",
            containerFactory = "hubMemberMutationDTOConcurrentKafkaListenerContainerFactory")
    public void memberMutationListener(@Payload HubMemberMutationDTO hubMemberMutationDTO) {
        log.info("Caught internal message for hub member mutation update for {}", hubMemberMutationDTO);

        switch (hubMemberMutationDTO.getState()) { // NOSONAR
            case ADDED ->
                    hubService.addMember(new ObjectId(hubMemberMutationDTO.getHubId()), hubMemberMutationDTO.getEmail());
            case REMOVED ->
                    hubService.removeMember(new ObjectId(hubMemberMutationDTO.getHubId()), hubMemberMutationDTO.getEmail());
        }
    }

    @KafkaListener(topics = MessagingConfiguration.HUB_MUTATION_TOPIC,
            groupId = "cs-activity-notifications-mgmt:hub-mutation-listener-group",
            autoStartup = "true",
            containerFactory = "hubMutationDTOConcurrentKafkaListenerContainerFactory")
    public void hubCreationListener(@Payload HubMutationDTO hubMutationDTO) {
        log.info("Caught internal message for hub creation update for {}", hubMutationDTO);

        switch (hubMutationDTO.getState()) { // NOSONAR
            case CREATED ->
                    hubService.createHub(hubMutationDTO);
            case DELETED -> hubService.deleteHub(new ObjectId(hubMutationDTO.getHubId()));
        }
    }

}
