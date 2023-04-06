package edu.pdae.cs.accountmgmt.controller;

import edu.pdae.cs.accountmgmt.model.dto.UserCreationDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationResponseDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserDetailsDTO;
import edu.pdae.cs.accountmgmt.repository.UserRepository;
import edu.pdae.cs.accountmgmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
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
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreationResponseDTO create(@RequestBody UserCreationDTO newUser) {
        return userService.register(newUser);
    }

    @GetMapping("/{id}")
    public UserDetailsDTO get(@PathVariable("id") ObjectId id) {
        return modelMapper.map(userRepository.findById(id).orElseThrow(), UserDetailsDTO.class);
    }

    @GetMapping
    public List<UserDTO> gets(@RequestParam("email") Optional<String> email) {
        return email
                .map(s -> Collections.singletonList(modelMapper.map(userRepository.findByEmail(s).orElseThrow(), UserDTO.class)))
                .orElseGet(() -> userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDTO.class)).toList());
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
