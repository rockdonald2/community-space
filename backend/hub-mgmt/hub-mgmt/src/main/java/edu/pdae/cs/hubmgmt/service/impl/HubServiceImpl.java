package edu.pdae.cs.hubmgmt.service.impl;

import edu.pdae.cs.hubmgmt.controller.exception.ConflictingOperationException;
import edu.pdae.cs.hubmgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.hubmgmt.model.Hub;
import edu.pdae.cs.hubmgmt.model.dto.*;
import edu.pdae.cs.hubmgmt.repository.HubRepository;
import edu.pdae.cs.hubmgmt.service.HubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubServiceImpl implements HubService {

    private final ModelMapper modelMapper;
    private final HubRepository hubRepository;

    @Override
    @CacheEvict(value = {"hub", "hubs"}, allEntries = true)
    public HubCreationResponseDTO create(HubCreationDTO hubCreationDTO, String asUser) throws IllegalArgumentException {
        if (hubCreationDTO.getName() == null || hubCreationDTO.getName().length() < 3) {
            throw new IllegalArgumentException("Hub name must be at least 3 characters long");
        }

        final Hub reqHub = modelMapper.map(hubCreationDTO, Hub.class);
        reqHub.setCreatedOn(new Date());
        reqHub.setOwner(asUser);
        reqHub.setMembers(new ArrayList<>(Collections.singletonList(asUser))); // add owner as first member

        final Hub createdHub = hubRepository.save(reqHub);
        return modelMapper.map(createdHub, HubCreationResponseDTO.class);
    }

    @Override
    @Cacheable(value = "hub")
    public HubDetailsDTO getById(ObjectId id, String asUser) {
        final Hub hub = hubRepository.findById(id).orElseThrow();
        final HubDetailsDTO hubDetailsDTO = modelMapper.map(hub, HubDetailsDTO.class);

        if (hub.getOwner().equals(asUser)) {
            hubDetailsDTO.setRole(Role.OWNER);
        } else if (hub.getMembers().contains(asUser)) {
            hubDetailsDTO.setRole(Role.MEMBER);
        } else if (hub.getWaiting().contains(asUser)) {
            hubDetailsDTO.setRole(Role.PENDING);
        } else {
            hubDetailsDTO.setRole(Role.NONE);
        }

        if (hubDetailsDTO.getRole().equals(Role.PENDING) || hubDetailsDTO.getRole().equals(Role.NONE)) {
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

        return hubDetailsDTO;
    }

    @Override
    @Cacheable("hubs")
    public List<HubDTO> getAll(String asUser) {
        return hubRepository.findAll().stream().map(hub -> {
            final HubDTO hubDTO = modelMapper.map(hub, HubDTO.class);

            if (hub.getOwner().equals(asUser)) {
                hubDTO.setRole(Role.OWNER);
            } else if (hub.getMembers().contains(asUser)) {
                hubDTO.setRole(Role.MEMBER);
            } else if (hub.getWaiting().contains(asUser)) {
                hubDTO.setRole(Role.PENDING);
            } else {
                hubDTO.setRole(Role.NONE);
            }

            return hubDTO;
        }).toList();
    }

    @Override
    @CacheEvict(value = {"hub", "hubs"}, allEntries = true)
    public HubCreationResponseDTO update(ObjectId id, HubUpdateDTO hubUpdateDTO, String asUser) throws ForbiddenOperationException {
        final Hub hub = hubRepository.findById(id).orElseThrow();

        if (!hub.getOwner().equals(asUser)) {
            log.warn("User {} is not the owner of hub {}", asUser, id);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        boolean hasChanged = false;

        if (hubUpdateDTO.getDescription() != null) {
            hub.setDescription(hubUpdateDTO.getDescription());
            hasChanged = true;
        }

        if (hasChanged) {
            hubRepository.save(hub);
        }

        return modelMapper.map(hub, HubCreationResponseDTO.class);
    }

    @Override
    @CacheEvict(value = {"hub", "hubs"}, allEntries = true)
    public void deleteById(ObjectId id, String asUser) throws ForbiddenOperationException, NoSuchElementException {
        if (!hubRepository.existsById(id)) {
            log.warn("Hub {} does not exist", id);
            throw new NoSuchElementException("Hub does not exist");
        }

        if (!hubRepository.findById(id).orElseThrow().getOwner().equals(asUser)) {
            log.warn("User {} is not the owner of hub {}", asUser, id);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        hubRepository.deleteById(id);
    }

    @Override
    @Cacheable("members")
    public List<MemberDTO> getMembers(ObjectId hubId, String asUser) throws ForbiddenOperationException {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();

        if (!hub.getMembers().contains(asUser)) {
            log.warn("User {} is not a member of hub {}", asUser, hubId);
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

        return hub.getMembers().stream().map(MemberDTO::new).toList();
    }

    @Override
    @Cacheable("member")
    public MemberDTO getMember(ObjectId hubId, String email, String asUser) throws ForbiddenOperationException {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();

        if (!hub.getMembers().contains(asUser)) {
            log.warn("User {} is not a member of hub {}", asUser, hubId);
            throw new ForbiddenOperationException("You are not a member of this hub");
        }

        return new MemberDTO(hub.getMembers().stream().filter(email::equals).findFirst().orElseThrow());
    }

    @Override
    @CacheEvict(value = {"waiters", "members", "member", "hubs", "hub"}, allEntries = true)
    public void addMember(ObjectId hubId, MemberDTO memberDTO, String asUser) throws ForbiddenOperationException {
        Objects.requireNonNull(memberDTO.getEmail(), "Email must not be null");
        final Hub hub = hubRepository.findById(hubId).orElseThrow();

        if (!hub.getOwner().equals(asUser)) {
            log.warn("User {} is not the owner of hub {}", asUser, hubId);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        if (hub.getMembers().contains(memberDTO.getEmail())) {
            log.warn("User {} is already a member of hub {}", memberDTO.getEmail(), hubId);
            throw new ConflictingOperationException("This user is already a member of this hub");
        }

        hub.getMembers().add(memberDTO.getEmail());
        hubRepository.save(hub);
    }

    @Override
    @CacheEvict(value = {"waiters", "members", "member", "hubs", "hub"}, allEntries = true)
    public void deleteMember(ObjectId hubId, String email, String asUser) throws ForbiddenOperationException {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();

        if (!hub.getOwner().equals(asUser)) {
            log.warn("User {} is not the owner of hub {}", asUser, hubId);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        hub.getMembers().remove(email);
        hubRepository.save(hub);
    }

    @Override
    @Cacheable("waiters")
    public List<MemberDTO> getWaiters(ObjectId hubId, String asUser) throws ForbiddenOperationException {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();

        if (!hub.getOwner().equals(asUser)) {
            log.warn("User {} is not the owner of hub {}", asUser, hubId);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        return hub.getWaiting().stream().map(MemberDTO::new).toList();
    }

    @Override
    @CacheEvict(value = {"waiters", "hubs", "hub"}, allEntries = true)
    public void addWaiter(ObjectId hubId, MemberDTO memberDTO) {
        Objects.requireNonNull(memberDTO.getEmail(), "Email must not be null");
        final Hub hub = hubRepository.findById(hubId).orElseThrow();

        if (hub.getWaiting().contains(memberDTO.getEmail())) {
            log.warn("User {} is already waiting for hub {}", memberDTO.getEmail(), hubId);
            throw new ConflictingOperationException("This user is already waiting for this hub");
        }

        hub.getWaiting().add(memberDTO.getEmail());

        hubRepository.save(hub);
    }

    @Override
    @CacheEvict(value = {"waiters", "hubs", "hub"}, allEntries = true)
    public void deleteWaiter(ObjectId hubId, String email, String asUser) throws ForbiddenOperationException {
        final Hub hub = hubRepository.findById(hubId).orElseThrow();

        if (!hub.getOwner().equals(asUser)) {
            log.warn("User {} is not the owner of hub {}", asUser, hubId);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        hub.getWaiting().remove(email);
        hubRepository.save(hub);
    }

}
