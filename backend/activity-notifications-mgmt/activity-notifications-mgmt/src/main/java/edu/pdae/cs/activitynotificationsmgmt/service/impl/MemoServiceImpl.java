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

import java.util.Date;
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

}
