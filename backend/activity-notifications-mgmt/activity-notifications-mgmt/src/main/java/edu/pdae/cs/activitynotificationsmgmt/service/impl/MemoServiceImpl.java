package edu.pdae.cs.activitynotificationsmgmt.service.impl;

import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import edu.pdae.cs.activitynotificationsmgmt.repository.MemoRepository;
import edu.pdae.cs.activitynotificationsmgmt.service.MemoService;
import edu.pdae.cs.common.model.Visibility;
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
    public void createMemo(ObjectId memoId, String memoTitle, String ownerEmail, ObjectId hubId, Visibility visibility, Date dueDate) {
        final Memo memo = Memo.builder()
                .id(memoId)
                .title(memoTitle)
                .owner(ownerEmail)
                .hubId(hubId)
                .visibility(visibility)
                .dueDate(dueDate)
                .completions(new HashSet<>())
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
    public void updateMemo(ObjectId memoId, String memoTitle, Visibility visibility, Date dueDate) {
        final Memo memo = memoRepository.findById(memoId).orElseThrow();

        memo.setTitle(memoTitle);
        memo.setVisibility(visibility);
        memo.setDueDate(dueDate);

        memoRepository.save(memo);
    }

    @Override
    public List<Memo> getDueMemos() {
        return memoRepository.findAllByDueDateBetweenAndVisibility(new Date(), new Date(Instant.now().plus(3, ChronoUnit.HOURS).toEpochMilli()), Visibility.PUBLIC);
    }

}
