package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.model.dto.UserDTO;
import edu.pdae.cs.accountmgmt.service.StatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private static final String STATUS_SET = "cs:acc-mgmt:active:users";

    private final RedisTemplate<String, UserDTO> redisTemplate;

    private SetOperations<String, UserDTO> opsForSet;

    @PostConstruct
    private void setup() {
        opsForSet = redisTemplate.opsForSet();
    }

    @Override
    public Set<UserDTO> getAllActive() throws NullPointerException {
        return Objects.requireNonNull(opsForSet.members(STATUS_SET));
    }

    @Override
    public void putActive(UserDTO userDTO) {
        opsForSet.add(STATUS_SET, userDTO);
    }

    @Override
    public void putAllActive(Set<UserDTO> userDTOs) throws NullPointerException {
        opsForSet.pop(STATUS_SET, Objects.requireNonNull(opsForSet.size(STATUS_SET)));
        opsForSet.add(STATUS_SET, userDTOs.toArray(new UserDTO[]{}));
    }

    @Override
    public void removeActive(UserDTO userDTO) {
        opsForSet.remove(STATUS_SET, userDTO);
    }

}
