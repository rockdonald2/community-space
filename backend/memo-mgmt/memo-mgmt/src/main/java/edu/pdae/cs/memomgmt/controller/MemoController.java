package edu.pdae.cs.memomgmt.controller;

import edu.pdae.cs.common.util.PageWrapper;
import edu.pdae.cs.memomgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.memomgmt.model.Memo;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memos")
@Slf4j
public class MemoController {

    private static final int PAGE_SIZE = 10;

    private final MemoService memoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemoCreationResponseDTO create(@RequestBody MemoCreationDTO memoCreationDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Creating new memo from author {}", user);
        Objects.requireNonNull(user);
        return memoService.create(memoCreationDTO, user);
    }

    @GetMapping("/{id}")
    public MemoDetailsDTO get(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting memo {}", id);
        Objects.requireNonNull(user);
        return memoService.getById(id, user);
    }

    @GetMapping
    public ResponseEntity<List<MemoDTO>> gets(@RequestParam("createdAfter") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> createdAfter, @RequestParam("visibility") Optional<Memo.Visibility> visibility, @RequestParam("hubId") Optional<ObjectId> hubId, @RequestParam("page") Optional<Integer> page, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
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
        return memoService.update(id, memoUpdateDTO, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting memo {}", id);
        memoService.delete(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<String> forbiddenHandler() {
        return new ResponseEntity<>("Operation is not allowed", HttpStatus.FORBIDDEN);
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
