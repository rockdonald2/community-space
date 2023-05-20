package edu.pdae.cs.memomgmt.service.impl;

import edu.pdae.cs.memomgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import edu.pdae.cs.memomgmt.repository.MemoRepository;
import edu.pdae.cs.memomgmt.service.HubService;
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
    private final HubService hubService;

    @Override
    @CacheEvict(value = {"memo", "memos"}, allEntries = true)
    public MemoCreationResponseDTO create(MemoCreationDTO memoCreationDTO, String asUser) throws ForbiddenOperationException {
        if (!hubService.isMember(memoCreationDTO.getHubId(), asUser)) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

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

        if (!hubService.isMember(memo.getHubId(), asUser)) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

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

        if (!hubService.isMember(memo.getHubId(), asUser)) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

        if (!asUser.equals(memo.getAuthor())) {
            throw new ForbiddenOperationException("The requester is not the author");
        }

        memoRepository.deleteById(id);
    }

    @Override
    public void deleteAllByHubId(ObjectId hubId) {
        memoRepository.deleteAllByHubId(hubId);
    }

    @Override
    @Cacheable("memo")
    public MemoDetailsDTO getById(ObjectId id, String asUser) {
        final Memo memo = memoRepository.findById(id).orElseThrow();

        if (!hubService.isMember(memo.getHubId(), asUser)) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

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
        List<Memo> memos = memoRepository.getMemosByCreatedOnAfter(after);
        memos = filterForUser(memos, asUser);

        return memos.stream()
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllAfterAndByVisibility(Date after, Memo.Visibility visibility, String asUser) {
        List<Memo> memos;

        if (Memo.Visibility.PRIVATE.equals(visibility)) {
            memos = memoRepository.getMemosByCreatedOnAfterAndVisibilityAndAuthor(after, visibility, asUser);
        } else {
            memos = memoRepository.getMemosByCreatedOnAfterAndVisibility(after, visibility);
        }

        memos = filterForUser(memos, asUser);
        return memos.stream()
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAll(String asUser) {
        List<Memo> memos = memoRepository.findAll();
        memos = filterForUser(memos, asUser);

        return memos.stream()
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Memo.Visibility visibility, ObjectId hubId, String asUser) {
        List<Memo> memos = memoRepository.getMemosByCreatedOnAfterAndHubIdAndVisibility(after, hubId, visibility);
        memos = filterForUser(memos, asUser);

        return memos.stream()
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser) {
        List<Memo> memos = memoRepository.getMemosByCreatedOnAfterAndHubId(after, hubId);
        memos = filterForUser(memos, asUser);

        return memos.stream()
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Memo.Visibility visibility, String asUser) {
        List<Memo> memos = memoRepository.getMemosByHubIdAndVisibility(hubId, visibility);
        memos = filterForUser(memos, asUser);

        return memos.stream()
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    @Override
    @Cacheable("memos")
    public List<MemoDTO> getAllByHubId(ObjectId hubId, String asUser) {
        List<Memo> memos = memoRepository.getMemosByHubId(hubId);
        memos = filterForUser(memos, asUser);

        return memos.stream()
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .toList();
    }

    /**
     * Will filter out those memos that are not accessible for the user, because he is not a member of the hub, if he is the member of the hub, it is not public or he is not the author.
     *
     * @param memos  List of memos to filter
     * @param asUser The user to filter for
     * @return The filtered list of memos
     */
    private List<Memo> filterForUser(List<Memo> memos, String asUser) {
        return memos.stream()
                .filter(memo ->
                        hubService.isMember(memo.getHubId(), asUser)
                                &&
                                (Memo.Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor())))
                .toList();
    }

}
