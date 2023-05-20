package edu.pdae.cs.memomgmt.service.impl;

import edu.pdae.cs.memomgmt.model.Hub;
import edu.pdae.cs.memomgmt.repository.HubRepository;
import edu.pdae.cs.memomgmt.service.HubService;
import edu.pdae.cs.memomgmt.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {

    private final HubRepository hubRepository;

    @Autowired
    @Lazy
    private MemoService memoService;

    @Override
    @CacheEvict({"hub", "memo", "memos"})
    public void createHub(ObjectId hubId, String ownerEmail) {
        hubRepository.save(Hub.builder()
                .id(hubId)
                .owner(ownerEmail)
                .members(new HashSet<>())
                .build());
    }

    @Override
    @CacheEvict({"hub", "memo", "memos"})
    public void deleteHub(ObjectId hubId) {
        // we need to delete all the memos as well
        memoService.deleteAllByHubId(hubId);
        hubRepository.deleteById(hubId);
    }

    @Override
    @CacheEvict({"hub", "memo", "memos"})
    public void addMember(ObjectId hubId, String email) {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();
        hub.getMembers().add(email);
        hubRepository.save(hub);
    }

    @Override
    @CacheEvict({"hub", "memo", "memos"})
    public void removeMember(ObjectId hubId, String email) {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();
        hub.getMembers().remove(email);
        hubRepository.save(hub);
    }

    @Override
    @Cacheable("hub")
    public boolean isMember(ObjectId hubId, String email) {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();
        return hub.getMembers().contains(email) || hub.getOwner().equals(email);
    }

}
