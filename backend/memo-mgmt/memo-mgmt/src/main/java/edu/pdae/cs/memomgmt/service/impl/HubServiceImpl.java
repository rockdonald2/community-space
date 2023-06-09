package edu.pdae.cs.memomgmt.service.impl;

import edu.pdae.cs.common.model.dto.HubMutationDTO;
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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {

    private final HubRepository hubRepository;

    @Autowired
    @Lazy
    private MemoService memoService;

    @Override
    @CacheEvict({"hub", "memo", "memos", "member"})
    public void createHub(HubMutationDTO hubMutationDTO) {
        hubRepository.save(Hub.builder()
                .id(new ObjectId(hubMutationDTO.getHubId()))
                .owner(hubMutationDTO.getOwner())
                .name(hubMutationDTO.getHubName())
                .members(new HashSet<>())
                .build());
    }

    @Override
    @CacheEvict({"hub", "memo", "memos", "member"})
    public void deleteHub(ObjectId hubId) {
        // we need to delete all the memos as well
        memoService.deleteAllByHubId(hubId);
        hubRepository.deleteById(hubId);
    }

    @Override
    @CacheEvict({"member", "memo", "memos"})
    public void addMember(ObjectId hubId, String email) {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();
        hub.getMembers().add(email);
        hubRepository.save(hub);
    }

    @Override
    @CacheEvict({"memo", "memos", "member"})
    public void removeMember(ObjectId hubId, String email) {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();
        hub.getMembers().remove(email);
        hubRepository.save(hub);
    }

    @Override
    @Cacheable("member")
    public boolean isMember(ObjectId hubId, String email) {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();
        return hub.getMembers().contains(email) || hub.getOwner().equals(email);
    }

    @Override
    @Cacheable("hub")
    public Hub getHub(ObjectId hubId) throws NoSuchElementException {
        return hubRepository.findById(hubId).orElseThrow();
    }

}
