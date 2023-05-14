package edu.pdae.cs.memomgmt.service.impl;

import edu.pdae.cs.memomgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import edu.pdae.cs.memomgmt.repository.MemoRepository;
import edu.pdae.cs.memomgmt.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(value = {"memo", "memos"}, allEntries = true)
    public MemoCreationResponseDTO create(MemoCreationDTO memoCreationDTO, String asUser) throws ForbiddenOperationException {
        final Memo reqMemo = modelMapper.map(memoCreationDTO, Memo.class);
        reqMemo.setCreatedOn(new Date());
        reqMemo.setId(null);
        reqMemo.setAuthor(asUser);

        final Memo createdMemo = memoRepository.save(reqMemo);
        return modelMapper.map(createdMemo, MemoCreationResponseDTO.class);
    }

    @Override
    @CacheEvict(value = {"memo", "memos"}, allEntries = true)
    public MemoCreationResponseDTO update(ObjectId id, MemoUpdateDTO memoUpdateDTO, String asUser) throws ForbiddenOperationException {
        final Memo memo = memoRepository.findById(id).orElseThrow();

        if (!asUser.equals(memo.getAuthor())) {
            throw new ForbiddenOperationException("The requester is not the author");
        }

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

    @Override
    @CacheEvict(value = {"memo", "memos"}, allEntries = true)
    public void delete(ObjectId id, String asUser) throws ForbiddenOperationException {
        final Memo memo = memoRepository.findById(id).orElseThrow();

        if (!asUser.equals(memo.getAuthor())) {
            throw new ForbiddenOperationException("The requester is not the author");
        }

        memoRepository.deleteById(id);
    }

    @Override
    @Cacheable("memo")
    public MemoDetailsDTO getById(ObjectId id, String asUser) {
        final Memo memo = memoRepository.findById(id).orElseThrow();

        if (Memo.Visibility.PRIVATE.equals(memo.getVisibility()) && !asUser.equals(memo.getAuthor())) {
            throw new ForbiddenOperationException("Memo is private and the requester is not the author");
        }

        return modelMapper.map(memo, MemoDetailsDTO.class);
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllByVisibility(Memo.Visibility visibility, String asUser) {
        if (Memo.Visibility.PRIVATE.equals(visibility)) {
            return memoRepository.getMemosByAuthorAndVisibility(asUser, visibility).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
        }

        return memoRepository.getMemosByVisibility(visibility).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllAfter(Date after, String asUser) {
        return memoRepository.getMemosByCreatedOnAfter(after).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllAfterAndByVisibility(Date after, Memo.Visibility visibility, String asUser) {
        if (Memo.Visibility.PRIVATE.equals(visibility)) {
            return memoRepository.getMemosByCreatedOnAfterAndVisibilityAndAuthor(after, visibility, asUser).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
        }

        return memoRepository.getMemosByCreatedOnAfterAndVisibility(after, visibility).stream().map(memo -> modelMapper.map(memo, MemoDTO.class)).toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAll(String asUser) {
        return memoRepository.findAll().stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Memo.Visibility visibility, ObjectId hubId, String asUser) {
        return memoRepository.getMemosByCreatedOnAfterAndHubIdAndVisibility(after, hubId, visibility).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser) {
        return memoRepository.getMemosByCreatedOnAfterAndHubId(after, hubId).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Memo.Visibility visibility, String asUser) {
        return memoRepository.getMemosByHubIdAndVisibility(hubId, visibility).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllByHubId(ObjectId hubId, String asUser) {
        return memoRepository.getMemosByHubId(hubId).stream()
                .filter(memo -> Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor()))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

}
