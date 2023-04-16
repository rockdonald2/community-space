package edu.pdae.cs.memomgmt.controller;

import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import edu.pdae.cs.memomgmt.repository.MemoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memos")
@Slf4j
public class MemoController {

    private final MemoRepository memoRepository;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemoCreationResponseDTO create(@RequestBody MemoCreationDTO memoCreationDTO) {
        log.info("Creating new memo from author {}", memoCreationDTO.getAuthor());

        final Memo reqMemo = modelMapper.map(memoCreationDTO, Memo.class);
        reqMemo.setCreatedOn(new Date());

        final Memo createdMemo = memoRepository.save(reqMemo);

        return modelMapper.map(createdMemo, MemoCreationResponseDTO.class);
    }

    @GetMapping("/{id}")
    public MemoDetailsDTO get(@PathVariable("id") ObjectId id) {
        return modelMapper.map(memoRepository.findById(id).orElseThrow(), MemoDetailsDTO.class);
    }

    @GetMapping
    public List<MemoDTO> gets() {
        return memoRepository.findAll().stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
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
    public void delete(@PathVariable("id") ObjectId id) {
        memoRepository.deleteById(id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> noSuchElementHandler() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
