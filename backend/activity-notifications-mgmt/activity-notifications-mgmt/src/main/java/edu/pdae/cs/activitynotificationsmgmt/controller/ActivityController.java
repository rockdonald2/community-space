package edu.pdae.cs.activitynotificationsmgmt.controller;

import edu.pdae.cs.activitynotificationsmgmt.model.GroupBy;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityGroupedDTO;
import edu.pdae.cs.activitynotificationsmgmt.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activities")
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public List<ActivityDTO> gets(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        log.info("Getting all activities from {} to {}", from, to);
        return activityService.getActivities(from, to);
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
