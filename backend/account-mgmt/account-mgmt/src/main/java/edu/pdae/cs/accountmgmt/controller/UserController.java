package edu.pdae.cs.accountmgmt.controller;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationResponseDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserDetailsDTO;
import edu.pdae.cs.accountmgmt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreationResponseDTO create(@RequestBody UserCreationDTO newUser) {
        log.info("Incoming registration request for {}", newUser.getEmail());
        return userService.register(newUser);
    }

    @GetMapping("/{id}")
    public UserDetailsDTO get(@PathVariable("id") ObjectId id) {
        log.info("Getting user {}", id);
        return userService.findById(id);
    }

    @GetMapping
    public List<UserDTO> gets(@RequestParam("email") Optional<String> email) {
        log.info("Getting all users");
        return email
                .map(s -> Collections.singletonList(userService.findByEmail(s)))
                .orElseGet(userService::findAll);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") ObjectId id) {
        log.info("Deleting user {}", id);
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFoundHandler() {
        return new ResponseEntity<>("Requested user cannot be found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<String> mongoHandler(MongoWriteException e) {
        if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
            return new ResponseEntity<>("User with the same e-mail address already exists", HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>("Error occurred while processing request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
