package edu.pdae.cs.hubmgmt.controller;

import com.mongodb.MongoWriteException;
import edu.pdae.cs.hubmgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.hubmgmt.model.Hub;
import edu.pdae.cs.hubmgmt.model.dto.*;
import edu.pdae.cs.hubmgmt.repository.HubRepository;
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
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
@Slf4j
public class HubController {

    private final HubRepository hubRepository;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HubCreationResponseDTO create(@RequestBody HubCreationDTO hubCreationDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        Objects.requireNonNull(user);
        log.info("Creating new hub {}", hubCreationDTO.getName());

        final Hub reqHub = modelMapper.map(hubCreationDTO, Hub.class);
        reqHub.setCreatedOn(new Date());
        reqHub.setOwner(user);

        final Hub createdHub = hubRepository.save(reqHub);
        return modelMapper.map(createdHub, HubCreationResponseDTO.class);
    }

    @GetMapping("/{id}")
    public HubDetailsDTO get(@PathVariable("id") ObjectId id) {
        log.info("Getting hub {}", id);
        return modelMapper.map(hubRepository.findById(id).orElseThrow(), HubDetailsDTO.class);
    }

    @GetMapping
    public List<HubDTO> gets() {
        log.info("Getting all hubs");
        return hubRepository.findAll().stream().map(hub -> modelMapper.map(hub, HubDTO.class)).toList();
    }

    @PatchMapping("/{id}")
    public HubCreationResponseDTO update(@PathVariable("id") ObjectId id, @Valid @RequestBody HubUpdateDTO hubUpdateDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Updating hub {}", id);

        final Hub hub = hubRepository.findById(id).orElseThrow();

        if (!hub.getOwner().equals(user)) {
            log.warn("User {} is not the owner of hub {}", user, id);
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting hub {}", id);

        if (!hubRepository.existsById(id)) {
            log.warn("Hub {} does not exist", id);
            throw new NoSuchElementException("Hub does not exist");
        }

        if (!hubRepository.findById(id).orElseThrow().getOwner().equals(user)) {
            log.warn("User {} is not the owner of hub {}", user, id);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        hubRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/members")
    public List<MemberDTO> getMembers(@PathVariable("id") ObjectId id) {
        log.info("Getting members of hub {}", id);
        final Hub hub = hubRepository.findById(id).orElseThrow();
        return hub.getMembers().stream().map(MemberDTO::new).toList();
    }

    @GetMapping("/{id}/members/{email}")
    public MemberDTO getMember(@PathVariable("id") ObjectId id, @PathVariable("email") String email) {
        log.info("Getting member {} of hub {}", email, id);
        final Hub hub = hubRepository.findById(id).orElseThrow();
        return new MemberDTO(hub.getMembers().stream().filter(email::equals).findFirst().orElseThrow());
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addMember(@PathVariable("id") ObjectId id, @RequestBody MemberDTO memberDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Adding member {} to hub {}", memberDTO.getEmail(), id);
        final Hub hub = hubRepository.findById(id).orElseThrow();

        if (!hub.getOwner().equals(user)) {
            log.warn("User {} is not the owner of hub {}", user, id);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        hub.getMembers().add(memberDTO.getEmail());
        hubRepository.save(hub);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/members/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMember(@PathVariable("id") ObjectId id, @PathVariable("email") String email, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting member {} from hub {}", email, id);
        final Hub hub = hubRepository.findById(id).orElseThrow();

        if (!hub.getOwner().equals(user)) {
            log.warn("User {} is not the owner of hub {}", user, id);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        hub.getMembers().remove(email);
        hubRepository.save(hub);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/waiters")
    public List<MemberDTO> getWaiters(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting waiters of hub {}", id);
        final Hub hub = hubRepository.findById(id).orElseThrow();

        if (!hub.getOwner().equals(user)) {
            log.warn("User {} is not the owner of hub {}", user, id);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        return hub.getWaiting().stream().map(MemberDTO::new).toList();
    }

    @PostMapping("/{id}/waiters")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addWaiter(@PathVariable("id") ObjectId id, @RequestBody MemberDTO memberDTO) {
        log.info("Adding waiter {} to hub {}", memberDTO.getEmail(), id);
        final Hub hub = hubRepository.findById(id).orElseThrow();
        hub.getWaiting().add(memberDTO.getEmail());
        hubRepository.save(hub);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/waiters/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteWaiter(@PathVariable("id") ObjectId id, @PathVariable("email") String email, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting waiter {} from hub {}", email, id);
        final Hub hub = hubRepository.findById(id).orElseThrow();

        if (!hub.getOwner().equals(user)) {
            log.warn("User {} is not the owner of hub {}", user, id);
            throw new ForbiddenOperationException("You are not the owner of this hub");
        }

        hub.getWaiting().remove(email);
        hubRepository.save(hub);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Void> nullHandler() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> noSuchElementHandler() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<Void> mongoWriteHandler() {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<Void> forbiddenOperationHandler() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
