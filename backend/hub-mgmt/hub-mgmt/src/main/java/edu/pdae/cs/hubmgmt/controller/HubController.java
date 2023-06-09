package edu.pdae.cs.hubmgmt.controller;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import edu.pdae.cs.common.util.UserWrapper;
import edu.pdae.cs.hubmgmt.controller.exception.ConflictingOperationException;
import edu.pdae.cs.hubmgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.hubmgmt.model.Role;
import edu.pdae.cs.hubmgmt.model.dto.*;
import edu.pdae.cs.hubmgmt.service.HubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
@Slf4j
public class HubController {

    private final HubService hubService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HubCreationResponseDTO create(@RequestBody HubCreationDTO hubCreationDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user, @RequestHeader("X-USER-NAME") String userName) {
        Objects.requireNonNull(user);
        log.info("Creating new hub {}", hubCreationDTO.getName());
        return hubService.create(hubCreationDTO, UserWrapper.builder().email(user).name(userName).build());
    }

    @GetMapping("/{id}")
    public HubDetailsDTO get(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting hub {}", id);
        return hubService.getById(id, user);
    }

    @GetMapping
    public List<HubDTO> gets(@RequestHeader("X-AUTH-TOKEN-SUBJECT") String user, @RequestParam("role") Optional<Role> role) {
        log.info("Getting all hubs");
        return hubService.getAll(user, role);
    }

    @PatchMapping("/{id}")
    public HubCreationResponseDTO update(@PathVariable("id") ObjectId id, @Valid @RequestBody HubUpdateDTO hubUpdateDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Updating hub {}", id);
        return hubService.update(id, hubUpdateDTO, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting hub {}", id);
        hubService.deleteById(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/members")
    public List<MemberDTO> getMembers(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting members of hub {}", id);
        return hubService.getMembers(id, user);
    }

    @GetMapping("/{id}/members/{email}")
    public MemberDTO getMember(@PathVariable("id") ObjectId id, @PathVariable("email") String email, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting member {} of hub {}", email, id);
        return hubService.getMember(id, email, user);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addMember(@PathVariable("id") ObjectId id, @RequestBody MemberDTO memberDTO, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Adding member {} to hub {}", memberDTO.getEmail(), id);
        hubService.addMember(id, memberDTO, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/members/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMember(@PathVariable("id") ObjectId id, @PathVariable("email") String email, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting member {} from hub {}", email, id);
        hubService.deleteMember(id, email, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/waiters")
    public List<MemberDTO> getWaiters(@PathVariable("id") ObjectId id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting waiters of hub {}", id);
        return hubService.getWaiters(id, user);
    }

    @PostMapping("/{id}/waiters")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addWaiter(@PathVariable("id") ObjectId id, @RequestBody MemberDTO memberDTO) {
        log.info("Adding waiter {} to hub {}", memberDTO.getEmail(), id);
        hubService.addWaiter(id, memberDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/waiters/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteWaiter(@PathVariable("id") ObjectId id, @PathVariable("email") String email, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Deleting waiter {} from hub {}", email, id);
        hubService.deleteWaiter(id, email, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public ResponseEntity<String> nullHandler() {
        return ResponseEntity.badRequest().body("Invalid request received");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementHandler() {
        return new ResponseEntity<>("Requested hub cannot be found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictingOperationException.class)
    public ResponseEntity<String> conflictingOperationHandler() {
        return new ResponseEntity<>("Conflicting operation attempted", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<String> mongoHandler(MongoWriteException e) {
        if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
            return new ResponseEntity<>("Hub with the same name already exists", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("Error occurred while processing request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<String> forbiddenOperationHandler() {
        return new ResponseEntity<>("Operation is not allowed", HttpStatus.FORBIDDEN);
    }

}
