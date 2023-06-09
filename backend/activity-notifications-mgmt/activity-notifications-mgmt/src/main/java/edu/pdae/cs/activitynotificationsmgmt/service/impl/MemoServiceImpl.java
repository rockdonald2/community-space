package edu.pdae.cs.activitynotificationsmgmt.service.impl;

import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import edu.pdae.cs.activitynotificationsmgmt.repository.MemoRepository;
import edu.pdae.cs.activitynotificationsmgmt.service.MemoService;
import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.model.dto.MemoMutationDTO;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;

    @Override
    @CacheEvict("memo")
    public void createMemo(MemoMutationDTO memoMutationDTO) {
        final Memo memo = Memo.builder()
                .id(new ObjectId(memoMutationDTO.getMemoId()))
                .title(memoMutationDTO.getTitle())
                .owner(memoMutationDTO.getOwner())
                .hubId(new ObjectId(memoMutationDTO.getHubId()))
                .visibility(memoMutationDTO.getVisibility())
                .dueDate(memoMutationDTO.getDueDate())
                .completions(new HashSet<>())
                .archived(memoMutationDTO.getIsArchived())
                .build();
        memoRepository.save(memo);
    }

    @Override
    @CacheEvict("memo")
    public void deleteMemo(ObjectId memoId) {
        memoRepository.deleteById(memoId);
    }

    @Override
    @Cacheable("memo")
    public Memo getMemo(ObjectId memoId) throws NoSuchElementException {
        return memoRepository.findById(memoId).orElseThrow();
    }

    @Override
    @CacheEvict("memo")
    public void deleteAllByHubId(ObjectId hubId) {
        memoRepository.deleteAllByHubId(hubId);
    }

    @Override
    public void addCompletion(ObjectId memoId, String userEmail) {
        final Memo memo = memoRepository.findById(memoId).orElseThrow();

        memo.getCompletions().add(userEmail);

        memoRepository.save(memo);
    }

    @Override
    public void updateMemo(MemoMutationDTO memoMutationDTO) {
        final Memo memo = memoRepository.findById(new ObjectId(memoMutationDTO.getMemoId())).orElseThrow();

        memo.setTitle(memoMutationDTO.getTitle());
        memo.setVisibility(memoMutationDTO.getVisibility());
        memo.setDueDate(memoMutationDTO.getDueDate());
        memo.setArchived(memoMutationDTO.getIsArchived());

        memoRepository.save(memo);
    }

    @Override
    public List<Memo> getDueMemos() {
        return memoRepository.findAllByDueDateBetweenAndVisibilityAndArchived(new Date(), new Date(Instant.now().plus(3, ChronoUnit.HOURS).toEpochMilli()), Visibility.PUBLIC, false);
    }

}
