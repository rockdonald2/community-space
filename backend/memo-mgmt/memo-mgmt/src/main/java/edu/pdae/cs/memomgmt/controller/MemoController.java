package edu.pdae.cs.memomgmt.controller;

import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import edu.pdae.cs.memomgmt.repository.MemoRepository;
import edu.pdae.cs.memomgmt.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
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

    private final MemoRepository memoRepository;
    private final ModelMapper modelMapper;
    private final MemoService memoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemoCreationResponseDTO create(@RequestBody MemoCreationDTO memoCreationDTO) {
        log.info("Creating new memo from author {}", memoCreationDTO.getAuthor());

        final Memo reqMemo = modelMapper.map(memoCreationDTO, Memo.class);
        reqMemo.setCreatedOn(new Date());
        reqMemo.setId(null); // TODO: fix this

        final Memo createdMemo = memoRepository.save(reqMemo);
        return modelMapper.map(createdMemo, MemoCreationResponseDTO.class);
    }

    @GetMapping("/{id}")
    public MemoDetailsDTO get(@PathVariable("id") ObjectId id) {
        log.info("Getting memo {}", id);
        return modelMapper.map(memoRepository.findById(id).orElseThrow(), MemoDetailsDTO.class);
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
    public MemoCreationResponseDTO update(@PathVariable("id") ObjectId id, @Valid @RequestBody MemoUpdateDTO memoUpdateDTO) {
        log.info("Modifying existing memo {}", id);

        final Memo memo = memoRepository.findById(id).orElseThrow();

        boolean hasChanged = false;

        if (memoUpdateDTO.getTitle() != null) {
            memo.setTitle(memoUpdateDTO.getTitle());
            hasChanged = true;
        }

        if (memoUpdateDTO.getContent() != null) {
            memo.setContent(memoUpdateDTO.getContent());
            hasChanged = true;
        }

        if (memoUpdateDTO.getVisibility() != null) {
            memo.setVisibility(memoUpdateDTO.getVisibility());
            hasChanged = true;
        }

        if (memoUpdateDTO.getUrgency() != null) {
            memo.setUrgency(memoUpdateDTO.getUrgency());
            hasChanged = true;
        }

        if (hasChanged) {
            memoRepository.save(memo);
        }

        return modelMapper.map(memo, MemoCreationResponseDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") ObjectId id) {
        log.info("Deleting memo {}", id);
        memoRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Void> nullHandler() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> noSuchElementHandler() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
