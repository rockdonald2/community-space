package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.service.StatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private static final String STATUS_COLLECTION = "cs:acc-mgmt:status:users";

    private final RedisTemplate<String, UserPresenceDTO> redisTemplate;
    private HashOperations<String, String, UserPresenceDTO> opsForHash;

    @PostConstruct
    private void setup() {
        opsForHash = redisTemplate.opsForHash();
    }

    @Override
    public Set<UserPresenceDTO> getAllActive() throws NullPointerException {
        return opsForHash.keys(STATUS_COLLECTION).stream().map(user -> UserPresenceDTO.builder().email(user).build()).collect(Collectors.toSet());
    }

    @Override
    public void putActive(UserPresenceDTO userDTO) {
        userDTO.setLastSeen(new Date());
        opsForHash.put(STATUS_COLLECTION, userDTO.getEmail(), userDTO);
    }

    @Override
    public void removeInactive(UserPresenceDTO userDTO) {
        opsForHash.delete(STATUS_COLLECTION, userDTO.getEmail());
    }

    @Override
    public void removeInactives() {
        final Date currDate = new Date();

        try (final var cursor = opsForHash.scan(STATUS_COLLECTION, ScanOptions.scanOptions().match("*").build())) {
            while (cursor.hasNext()) {
                final var userPresenceDTO = cursor.next().getValue();

                final long diffInMs = currDate.getTime() - userPresenceDTO.getLastSeen().getTime();
                if (TimeUnit.MINUTES.convert(diffInMs, TimeUnit.MILLISECONDS) >= 5) {
                    removeInactive(userPresenceDTO);
                }
            }
        }
    }

}
