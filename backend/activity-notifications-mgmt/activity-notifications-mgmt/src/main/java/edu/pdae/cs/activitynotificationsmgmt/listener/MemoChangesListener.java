package edu.pdae.cs.activitynotificationsmgmt.listener;

import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
import edu.pdae.cs.activitynotificationsmgmt.service.MemoService;
import edu.pdae.cs.common.model.dto.MemoMutationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemoChangesListener {

    private final MemoService memoService;

    @KafkaListener(topics = MessagingConfiguration.MEMO_MUTATION_TOPIC,
            groupId = "cs-activity-notifications-mgmt:memo-mutation-listener-group",
            autoStartup = "true",
            containerFactory = "memoMutationDTOConcurrentKafkaListenerContainerFactory")
    public void memberMutationListener(@Payload MemoMutationDTO memoMutationDTO) {
        log.info("Caught internal message for memo mutation update for {}", memoMutationDTO);

        switch (memoMutationDTO.getState()) { // NOSONAR
            case CREATED ->
                    memoService.createMemo(new ObjectId(memoMutationDTO.getMemoId()), memoMutationDTO.getTitle(), memoMutationDTO.getOwner(), new ObjectId(memoMutationDTO.getHubId()), memoMutationDTO.getVisibility(), memoMutationDTO.getDueDate());
            case DELETED -> memoService.deleteMemo(new ObjectId(memoMutationDTO.getMemoId()));
        }
    }

}
