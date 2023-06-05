package edu.pdae.cs.memomgmt.controller;

import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.model.dto.MemoMutationDTO;
import edu.pdae.cs.common.util.PageWrapper;
import edu.pdae.cs.common.util.UserWrapper;
import edu.pdae.cs.memomgmt.config.MessagingConfiguration;
import edu.pdae.cs.memomgmt.controller.exception.ConflictingOperationException;
import edu.pdae.cs.memomgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.memomgmt.model.dto.*;
import edu.pdae.cs.memomgmt.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memos")
@Slf4j
public class MemoController {

    private static final int PAGE_SIZE = 10;

    private final MemoService memoService;
    private final KafkaTemplate<String, MemoMutationDTO> memoMutationDTOKafkaTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemoCreationResponseDTO create(@RequestBody MemoCreationDTO memoCreationDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user, @RequestHeader("X-USER-NAME") String userName) {
        log.info("Creating new memo from author {}", user);
        Objects.requireNonNull(user);
        final var createdDto = memoService.create(memoCreationDTO, UserWrapper.builder().name(userName).email(user).build());

        // send a message to Kafka about the creation
        memoMutationDTOKafkaTemplate.send(MessagingConfiguration.MEMO_MUTATION_TOPIC, MemoMutationDTO.builder()
                .state(MemoMutationDTO.State.CREATED)
                .hubId(createdDto.getHubId())
                .memoId(createdDto.getId())
                .title(createdDto.getTitle())
                .owner(user)
                .visibility(createdDto.getVisibility())
                .dueDate(createdDto.getDueDate())
                .build());

        return createdDto;
    }

    @GetMapping("/{id}")
    public MemoDetailsDTO get(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting memo {}", id);
        Objects.requireNonNull(user);
        return memoService.getById(id, user);
    }

    @GetMapping
    public ResponseEntity<List<MemoDTO>> gets(@RequestParam("createdAfter") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> createdAfter, @RequestParam("visibility") Optional<Visibility> visibility, @RequestParam("hubId") Optional<ObjectId> hubId, @RequestParam("page") Optional<Integer> page, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting all memos");
        Objects.requireNonNull(user);

        PageWrapper<MemoDTO> wrapper;
        if (createdAfter.isPresent() && visibility.isPresent() && hubId.isPresent()) {
            wrapper = memoService.getAllAfterByHubIdAndByVisibility(createdAfter.get(), visibility.get(), hubId.get(), user, page.orElse(0), PAGE_SIZE);
        } else if (createdAfter.isPresent() && hubId.isPresent()) {
            wrapper = memoService.getAllAfterByHubId(createdAfter.get(), hubId.get(), user, page.orElse(0), PAGE_SIZE);
        } else if (visibility.isPresent() && hubId.isPresent()) {
            wrapper = memoService.getAllByHubIdAndByVisibility(hubId.get(), visibility.get(), user, page.orElse(0), PAGE_SIZE);
        } else if (createdAfter.isPresent() && visibility.isPresent()) {
            wrapper = memoService.getAllAfterAndByVisibility(createdAfter.get(), visibility.get(), user, page.orElse(0), PAGE_SIZE);
        } else if (createdAfter.isPresent()) {
            wrapper = memoService.getAllAfter(createdAfter.get(), user, page.orElse(0), PAGE_SIZE);
        } else if (visibility.isPresent()) {
            wrapper = memoService.getAllByVisibility(visibility.get(), user, page.orElse(0), PAGE_SIZE);
        } else if (hubId.isPresent()) {
            wrapper = memoService.getAllByHubId(hubId.get(), user, page.orElse(0), PAGE_SIZE);
        } else {
            wrapper = memoService.getAll(user, page.orElse(0), PAGE_SIZE);
        }

        final var headers = new HttpHeaders();
        headers.set("X-TOTAL-COUNT", String.valueOf(wrapper.getTotalNumberOfElements()));
        headers.set("X-TOTAL-PAGES", String.valueOf(wrapper.getTotalPages()));
        headers.set("X-PAGE-SIZE", String.valueOf(wrapper.getPageSize()));
        headers.set("Access-Control-Expose-Headers", "*");

        return ResponseEntity.ok()
                .headers(headers)
                .body(wrapper.getContent());
    }

    @PatchMapping("/{id}")
    public MemoCreationResponseDTO update(@PathVariable("id") ObjectId id, @Valid @RequestBody MemoUpdateDTO memoUpdateDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Modifying existing memo {}", id);
        Objects.requireNonNull(user);
        final var memo = memoService.update(id, memoUpdateDTO, user);

        // send a message to Kafka about the update
        memoMutationDTOKafkaTemplate.send(MessagingConfiguration.MEMO_MUTATION_TOPIC, MemoMutationDTO.builder()
                .memoId(memo.getId())
                .state(MemoMutationDTO.State.UPDATED)
                .title(memo.getTitle())
                .visibility(memo.getVisibility())
                .dueDate(memo.getDueDate())
                .build());

        return memo;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting memo {}", id);

        memoService.delete(id, user);

        // send a message to Kafka about the deletion
        memoMutationDTOKafkaTemplate.send(MessagingConfiguration.MEMO_MUTATION_TOPIC, MemoMutationDTO.builder()
                .memoId(id.toHexString())
                .state(MemoMutationDTO.State.DELETED)
                .build());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/completions")
    @ResponseStatus(HttpStatus.CREATED)
    public MemoCompletionResponseDTO handleCompletion(@PathVariable("id") ObjectId id, @RequestBody MemoCompletionDTO memoCompletionDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String asUser, @RequestHeader("X-USER-NAME") String userName) {
        // add completion to a specific memo
        return memoService.completeMemo(id, memoCompletionDTO.getUser(), UserWrapper.builder().name(userName).email(asUser).build());
    }

    @GetMapping("/{id}/completions")
    public List<MemoCompletionResponseDTO> getCompletions(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String asUser) {
        // get all completions for a specific memo
        return memoService.getCompletions(id, asUser);
    }

    @GetMapping("/{id}/completions/{email}")
    public MemoCompletionResponseDTO verifyCompletion(@PathVariable("id") ObjectId id, @PathVariable("email") String userToVerify, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String asUser) {
        // verify if a user has completed a memo
        return memoService.verifyCompletion(id, userToVerify, asUser);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<String> forbiddenHandler() {
        return new ResponseEntity<>("Operation is not allowed", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictingOperationException.class)
    public ResponseEntity<String> conflictHandler() {
        return new ResponseEntity<>("Operation is conflicting", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullHandler() {
        return ResponseEntity.badRequest().body("Invalid request received");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementHandler() {
        return new ResponseEntity<>("Requested memo cannot be found", HttpStatus.NOT_FOUND);
    }

}
