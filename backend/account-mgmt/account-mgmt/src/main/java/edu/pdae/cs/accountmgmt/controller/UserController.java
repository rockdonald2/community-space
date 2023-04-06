package edu.pdae.cs.accountmgmt.controller;

import edu.pdae.cs.accountmgmt.model.User;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationDTO;
import edu.pdae.cs.accountmgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody UserCreationDTO newUser) {
        final User mappedUser = modelMapper.map(newUser, User.class);
        return userRepository.save(mappedUser);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable("id") ObjectId id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping
    public List<User> gets(@RequestParam("email") String email) {
        if (email == null) {
            return userRepository.findAll();
        }

        return Collections.singletonList(userRepository.findByEmail(email).orElseThrow());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") ObjectId id) {
        userRepository.deleteById(id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFoundHandler() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
