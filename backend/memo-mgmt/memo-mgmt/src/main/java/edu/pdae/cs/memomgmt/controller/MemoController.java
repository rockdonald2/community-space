package edu.pdae.cs.memomgmt.controller;

import edu.pdae.cs.memomgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import edu.pdae.cs.memomgmt.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memos")
@Slf4j
public class MemoController {

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
    public List<MemoDTO> gets(@RequestParam("createdAfter") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> createdAfter, @RequestParam("visibility") Optional<Memo.Visibility> visibility, @RequestParam("hubId") Optional<ObjectId> hubId, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        Objects.requireNonNull(user);
        log.info("Getting all memos");

        if (createdAfter.isPresent() && visibility.isPresent() && hubId.isPresent()) {
            return memoService.getAllAfterByHubIdAndByVisibility(createdAfter.get(), visibility.get(), hubId.get(), user);
        } else if (createdAfter.isPresent() && hubId.isPresent()) {
            return memoService.getAllAfterByHubId(createdAfter.get(), hubId.get(), user);
        } else if (visibility.isPresent() && hubId.isPresent()) {
            return memoService.getAllByHubIdAndByVisibility(hubId.get(), visibility.get(), user);
        } else if (createdAfter.isPresent() && visibility.isPresent()) {
            return memoService.getAllAfterAndByVisibility(createdAfter.get(), visibility.get(), user);
        } else if (createdAfter.isPresent()) {
            return memoService.getAllAfter(createdAfter.get(), user);
        } else if (visibility.isPresent()) {
            return memoService.getAllByVisibility(visibility.get(), user);
        } else if (hubId.isPresent()) {
            return memoService.getAllByHubId(hubId.get(), user);
        }

        return memoService.getAll(user); // ! find-all anti-pattern
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
