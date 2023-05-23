package edu.pdae.cs.activitynotificationsmgmt.controller;

import edu.pdae.cs.activitynotificationsmgmt.model.GroupBy;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityGroupedDTO;
import edu.pdae.cs.activitynotificationsmgmt.service.ActivityService;
import edu.pdae.cs.common.util.PageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activities")
@Slf4j
public class ActivityController {

    private static final int PAGE_SIZE = 25;

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> gets(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to, @RequestParam("page") Optional<Integer> page) {
        log.info("Getting all activities from {} to {}", from, to);
        final PageWrapper<ActivityDTO> activities = activityService.getActivities(from, to, page.orElse(0), PAGE_SIZE);

        final var headers = new HttpHeaders();
        headers.set("X-TOTAL-COUNT", String.valueOf(activities.getTotalNumberOfElements()));
        headers.set("X-TOTAL-PAGES", String.valueOf(activities.getTotalPages()));
        headers.set("X-PAGE-SIZE", String.valueOf(activities.getPageSize()));
        headers.set("Access-Control-Expose-Headers", "*");

        return ResponseEntity.ok().headers(headers).body(activities.getContent());
    }

    @GetMapping("/groups")
    public List<ActivityGroupedDTO> getsByGroups(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to, @RequestParam GroupBy groupBy) {
        log.info("Getting all activities grouped from {} to {}", from, to);
        return activityService.groupActivities(from, to, groupBy);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullHandler() {
        return ResponseEntity.badRequest().body("Invalid request received");
    }

}
