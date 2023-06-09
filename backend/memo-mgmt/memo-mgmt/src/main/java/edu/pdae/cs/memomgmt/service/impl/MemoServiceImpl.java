package edu.pdae.cs.memomgmt.service.impl;

import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.model.dto.ActivityFiredDTO;
import edu.pdae.cs.common.util.PageWrapper;
import edu.pdae.cs.common.util.UserWrapper;
import edu.pdae.cs.memomgmt.config.MessagingConfiguration;
import edu.pdae.cs.memomgmt.controller.exception.ConflictingOperationException;
import edu.pdae.cs.memomgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.memomgmt.model.Hub;
import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import edu.pdae.cs.memomgmt.repository.MemoRepository;
import edu.pdae.cs.memomgmt.service.HubService;
import edu.pdae.cs.memomgmt.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private static final int HARD_RETURN_LIMIT = 50;

    private final MemoRepository memoRepository;
    private final ModelMapper modelMapper;
    private final HubService hubService;
    private final KafkaTemplate<String, ActivityFiredDTO> activityFiredDTOKafkaTemplate;

    @Override
    @CacheEvict(value = {"memo", "memos"}, allEntries = true)
    public MemoCreationResponseDTO create(@Valid MemoCreationDTO memoCreationDTO, UserWrapper userWrapper) throws ForbiddenOperationException {
        if (!hubService.isMember(memoCreationDTO.getHubId(), userWrapper.getEmail())) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

        final Hub hub = hubService.getHub(memoCreationDTO.getHubId());

        final Memo reqMemo = modelMapper.map(memoCreationDTO, Memo.class);
        reqMemo.setCreatedOn(new Date());
        reqMemo.setId(null);
        reqMemo.setAuthor(userWrapper.getEmail());
        reqMemo.setAuthorName(userWrapper.getName());
        reqMemo.setCompletions(new HashSet<>());
        reqMemo.setArchived(false);

        if (memoCreationDTO.getDueDate() != null) {
            reqMemo.setDueDate(memoCreationDTO.getDueDate());
        }

        final Memo createdMemo = memoRepository.save(reqMemo);
        // send message to activity topic
        activityFiredDTOKafkaTemplate.send(MessagingConfiguration.ACTIVITY_TOPIC, ActivityFiredDTO.builder()
                .user(userWrapper.getEmail())
                .userName(userWrapper.getName())
                .hubId(memoCreationDTO.getHubId().toHexString())
                .hubName(hub.getName())
                .date(new Date())
                .type(Type.MEMO_CREATED)
                .memoId(createdMemo.getId().toHexString())
                .memoTitle(createdMemo.getTitle())
                .visibility(createdMemo.getVisibility())
                .build());

        return modelMapper.map(createdMemo, MemoCreationResponseDTO.class);
    }

    @Override
    @CacheEvict(value = {"memo", "memos"}, allEntries = true)
    public MemoCreationResponseDTO update(ObjectId id, @Valid MemoUpdateDTO memoUpdateDTO, String asUser) throws ForbiddenOperationException {
        final Memo memo = memoRepository.findById(id).orElseThrow();

        if (!hubService.isMember(memo.getHubId(), asUser)) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

        if (!asUser.equals(memo.getAuthor())) {
            throw new ForbiddenOperationException("The requester is not the author");
        }

        if (memoUpdateDTO.getDueDate() != null && memoUpdateDTO.getDueDate().before(new Date())) {
            throw new ConflictingOperationException("Due date cannot be in the past");
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

        if (memoUpdateDTO.getDueDate() != null && memoUpdateDTO.getDueDate().after(new Date())) {
            memo.setDueDate(memoUpdateDTO.getDueDate());
            hasChanged = true;
        } else if (memoUpdateDTO.getDueDate() == null) {
            memo.setDueDate(null); // this is the clear due date action
            hasChanged = true;
        }

        if (memoUpdateDTO.getArchived() != null) {
            memo.setArchived(memoUpdateDTO.getArchived());
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
    @CacheEvict(value = {"memo", "memos"}, allEntries = true)
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

        if (Visibility.PRIVATE.equals(memo.getVisibility()) && !asUser.equals(memo.getAuthor())) {
            throw new ForbiddenOperationException("Memo is private and the requester is not the author");
        }

        final MemoDetailsDTO memoDetailsDTO = modelMapper.map(memo, MemoDetailsDTO.class);
        memoDetailsDTO.setCompleted(memo.getCompletions().contains(asUser));

        return memoDetailsDTO;
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAllByVisibility(Visibility visibility, String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByVisibilityAndArchived(visibility, archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAllAfter(Date after, String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByCreatedOnAfterAndArchived(after, archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAllAfterAndByVisibility(Date after, Visibility visibility, String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByCreatedOnAfterAndVisibilityAndArchived(after, visibility, archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAll(String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByArchived(archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Visibility visibility, ObjectId hubId, String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByCreatedOnAfterAndHubIdAndVisibilityAndArchived(after, hubId, visibility, archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByCreatedOnAfterAndHubIdAndArchived(after, hubId, archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Visibility visibility, String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByHubIdAndVisibilityAndArchived(hubId, visibility, archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @Cacheable("memos")
    public PageWrapper<MemoDTO> getAllByHubId(ObjectId hubId, String asUser, int currPage, int pageSize, boolean archived) {
        return constructPageWrapper(memoRepository.getMemosByHubIdAndArchived(hubId, archived, PageRequest.of(currPage, pageSize)), asUser);
    }

    @Override
    @CacheEvict(value = {"completion", "completions", "memo", "memos"}, allEntries = true)
    public MemoCompletionResponseDTO completeMemo(ObjectId memoId, String user, UserWrapper actionTakerUserWrapper) throws NoSuchElementException, ForbiddenOperationException, ConflictingOperationException {
        final Memo memo = memoRepository.findById(memoId).orElseThrow();

        if (!hubService.isMember(memo.getHubId(), actionTakerUserWrapper.getEmail())) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

        if (memo.getVisibility().equals(Visibility.PRIVATE)) {
            throw new ConflictingOperationException("The memo is private");
        }

        if (memo.getDueDate() != null && memo.getDueDate().before(new Date())) {
            throw new ConflictingOperationException("The memo is overdue");
        }

        if (memo.isArchived()) {
            throw new ConflictingOperationException("The memo is archived");
        }

        final Hub hub = hubService.getHub(memo.getHubId());

        if (actionTakerUserWrapper.getEmail().equals(memo.getAuthor())) {
            throw new ConflictingOperationException("The author cannot complete its own memo");
        }

        if (!user.equals(actionTakerUserWrapper.getEmail())) {
            throw new ForbiddenOperationException("You cannot complete a memo for another user");
        }

        if (memo.getCompletions().contains(user)) {
            throw new ConflictingOperationException("The memo is already completed by this user");
        }

        memo.getCompletions().add(user);
        memoRepository.save(memo);

        activityFiredDTOKafkaTemplate.send(MessagingConfiguration.ACTIVITY_TOPIC, ActivityFiredDTO.builder()
                .user(actionTakerUserWrapper.getEmail())
                .userName(actionTakerUserWrapper.getName())
                .date(new Date())
                .type(Type.MEMO_COMPLETED)
                .hubId(memo.getHubId().toHexString())
                .hubName(hub.getName())
                .memoId(memoId.toHexString())
                .memoTitle(memo.getTitle())
                .visibility(memo.getVisibility())
                .build());

        return MemoCompletionResponseDTO.builder()
                .memoId(memoId.toHexString())
                .hubId(memo.getHubId().toHexString())
                .user(user)
                .completed(true)
                .build();
    }

    @Override
    @Cacheable("completions")
    public List<MemoCompletionResponseDTO> getCompletions(ObjectId memoId, String asUser) throws NoSuchElementException, ForbiddenOperationException {
        final Memo memo = memoRepository.findById(memoId).orElseThrow();

        if (!asUser.equals(memo.getAuthor())) {
            throw new ForbiddenOperationException("The requester is not the author");
        }

        return memo.getCompletions().stream()
                .map(completion -> MemoCompletionResponseDTO.builder()
                        .memoId(memoId.toHexString())
                        .hubId(memo.getHubId().toHexString())
                        .user(completion)
                        .completed(true)
                        .build())
                .toList();
    }

    @Override
    @Cacheable("completion")
    public MemoCompletionResponseDTO verifyCompletion(ObjectId memoId, String userToVerify, String asUser) throws NoSuchElementException, ForbiddenOperationException {
        final Memo memo = memoRepository.findById(memoId).orElseThrow();

        if (!asUser.equals(memo.getAuthor()) && !asUser.equals(userToVerify)) {
            throw new ForbiddenOperationException("The requester is not the author or you can't verify another user's completion");
        }

        return MemoCompletionResponseDTO.builder()
                .memoId(memoId.toHexString())
                .hubId(memo.getHubId().toHexString())
                .user(userToVerify)
                .completed(memo.getCompletions().contains(userToVerify))
                .build();
    }

    /**
     * Will filter out those memos that are not accessible for the user, because he is not a member of the hub, if he is the member of the hub, it is not public, or he is not the author.
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
                                (Visibility.PUBLIC.equals(memo.getVisibility()) || asUser.equals(memo.getAuthor())))
                .toList();
    }

    /**
     * Will construct a page wrapper for the given page of memos.
     *
     * @param memoPage The page of memos
     * @param asUser   The user to filter for
     * @return The page wrapper
     */
    private PageWrapper<MemoDTO> constructPageWrapper(Page<Memo> memoPage, String asUser) {
        List<Memo> memos = memoPage.getContent();
        memos = filterForUser(memos, asUser);

        final var memoDTOs = memos.stream()
                .map(memo -> {
                    final var m = modelMapper.map(memo, MemoDTO.class);
                    m.setCompleted(memo.getCompletions().contains(asUser));
                    m.setArchived(memo.isArchived());
                    return m;
                })
                .toList();

        return PageWrapper.<MemoDTO>builder()
                .pageSize(memoPage.getSize())
                .totalNumberOfElements(memoPage.getTotalElements())
                .totalPages(memoPage.getTotalPages())
                .content(memoDTOs).build();
    }

}
