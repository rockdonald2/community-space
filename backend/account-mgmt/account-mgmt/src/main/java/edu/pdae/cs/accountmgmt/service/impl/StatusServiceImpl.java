package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.service.StatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusServiceImpl implements StatusService {

    private static final String STATUS_COLLECTION = "cs:acc-mgmt:status:users";
    private static final String STATUS_INACTIVE_LAST_MODIFIED = "cs:acc-mgmt:status:inactive:last-modified";
    private static final String STATUS_ACTIVE_LAST_MODIFIED = "cs:acc-mgmt:status:active:last-modified";

    private final RedisTemplate<String, UserPresenceDTO> redisTemplatePresence;
    private final RedisTemplate<String, Date> redisTemplateLastModified;
    private ValueOperations<String, Date> opsForValueLastModified;
    private HashOperations<String, String, UserPresenceDTO> opsForHashPresence;

    @Value("${cs.status.cleanup.interval.minutes}")
    private int statusIntervalMinutes;

    @PostConstruct
    private void setup() {
        opsForHashPresence = redisTemplatePresence.opsForHash();
        opsForValueLastModified = redisTemplateLastModified.opsForValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserPresenceDTO> getAllActive() throws NullPointerException {
        log.info("Getting all active users");

        return opsForHashPresence.keys(STATUS_COLLECTION).stream().map(user -> UserPresenceDTO.builder().email(user).build()).collect(Collectors.toSet());
    }

    @Override
    public void putActive(UserPresenceDTO userDTO) {
        log.info("Putting {} as active", userDTO.getEmail());

        userDTO.setLastSeen(new Date());
        opsForHashPresence.put(STATUS_COLLECTION, userDTO.getEmail(), userDTO);
    }

    @Override
    public void removeInactive(UserPresenceDTO userDTO) {
        log.info("Putting {} as inactive", userDTO.getEmail());

        opsForHashPresence.delete(STATUS_COLLECTION, userDTO.getEmail());
    }

    @Override
    @Transactional
    public void removeInactives() {
        log.info("Cleaning inactive users");

        final Date lastModified = opsForValueLastModified.get(STATUS_INACTIVE_LAST_MODIFIED);
        final Date currDate = new Date();

        if (lastModified != null) {
            final long diffInMs = currDate.getTime() - lastModified.getTime();
            if (TimeUnit.MINUTES.convert(diffInMs, TimeUnit.MILLISECONDS) < statusIntervalMinutes) {
                log.info("Skipping removing inactives as someone has updated out of schedule");
                return;
            }
        }

        try (final var cursor = opsForHashPresence.scan(STATUS_COLLECTION, ScanOptions.scanOptions().match("*").build())) {
            while (cursor.hasNext()) {
                final var userPresenceDTO = cursor.next().getValue();

                final long diffInMs = currDate.getTime() - userPresenceDTO.getLastSeen().getTime();
                if (TimeUnit.MINUTES.convert(diffInMs, TimeUnit.MILLISECONDS) >= statusIntervalMinutes) {
                    removeInactive(userPresenceDTO);
                }
            }
        }
    }

}
