package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.service.StatusService;
import edu.pdae.cs.common.model.dto.UserPresenceDTO;
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

import java.util.Collections;
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
    private int cleanIntervalMinutes;
    @Value("${cs.status.broadcast.interval.minutes:1}")
    private int broadcastIntervalMinutes;

    @PostConstruct
    private void setup() {
        opsForHashPresence = redisTemplatePresence.opsForHash();
        opsForValueLastModified = redisTemplateLastModified.opsForValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserPresenceDTO> getAllActive(boolean force) throws NullPointerException {
        log.info("Getting all active users");

        final Date currDate = new Date();

        if (!force) {
            final Date lastModified = opsForValueLastModified.get(STATUS_ACTIVE_LAST_MODIFIED);

            if (lastModified != null) {
                final long diffInMs = currDate.getTime() - lastModified.getTime();
                if (TimeUnit.MINUTES.convert(diffInMs, TimeUnit.MILLISECONDS) < broadcastIntervalMinutes) {
                    log.info("Skipping getting all active users as someone has done it recently");
                    return Collections.emptySet();
                }
            }
        }

        final var entries = opsForHashPresence.keys(STATUS_COLLECTION).stream().map(user -> UserPresenceDTO.builder().email(user).build()).collect(Collectors.toSet());

        opsForValueLastModified.set(STATUS_ACTIVE_LAST_MODIFIED, currDate);
        return entries;
    }

    @Override
    @Transactional
    public void putActive(UserPresenceDTO userDTO) {
        log.info("Putting {} as active", userDTO.getEmail());

        userDTO.setLastSeen(new Date());
        opsForHashPresence.put(STATUS_COLLECTION, userDTO.getEmail(), userDTO);
    }

    @Override
    @Transactional
    public void removeInactive(UserPresenceDTO userDTO) {
        log.info("Putting {} as inactive", userDTO.getEmail());

        opsForHashPresence.delete(STATUS_COLLECTION, userDTO.getEmail());
    }

    @Override
    @Transactional
    public void removeInactives(boolean force) {
        log.info("Cleaning inactive users");

        final Date currDate = new Date();

        if (!force) {
            final Date lastModified = opsForValueLastModified.get(STATUS_INACTIVE_LAST_MODIFIED);

            if (lastModified != null) {
                final long diffInMs = currDate.getTime() - lastModified.getTime();
                if (TimeUnit.MINUTES.convert(diffInMs, TimeUnit.MILLISECONDS) < cleanIntervalMinutes) {
                    log.info("Skipping removing inactives as someone has updated out of schedule");
                    return;
                }
            }
        }

        try (final var cursor = opsForHashPresence.scan(STATUS_COLLECTION, ScanOptions.scanOptions().match("*").build())) {
            while (cursor.hasNext()) {
                final var userPresenceDTO = cursor.next().getValue();

                final long diffInMs = currDate.getTime() - userPresenceDTO.getLastSeen().getTime();
                if (TimeUnit.MINUTES.convert(diffInMs, TimeUnit.MILLISECONDS) >= cleanIntervalMinutes) {
                    removeInactive(userPresenceDTO);
                }
            }
        }

        opsForValueLastModified.set(STATUS_INACTIVE_LAST_MODIFIED, currDate);
    }

}
