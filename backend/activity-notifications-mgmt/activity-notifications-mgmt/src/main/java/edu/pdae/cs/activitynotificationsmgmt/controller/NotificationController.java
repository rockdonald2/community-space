package edu.pdae.cs.activitynotificationsmgmt.controller;

import edu.pdae.cs.activitynotificationsmgmt.controller.exception.ForbiddenOperationException;
import edu.pdae.cs.activitynotificationsmgmt.model.Notification;
import edu.pdae.cs.activitynotificationsmgmt.service.NotificationService;
import edu.pdae.cs.common.model.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDTO> gets(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Getting all notifications from {} to {} as user {}", from, to, user);

        final var usersNotifications = new ArrayList<>(notificationService.getNotifications(user, user, from, to));
        final var generalNotifications = notificationService.getNotifications(Notification.GroupTargets.GENERAL.getValue(), user, from, to);

        usersNotifications.addAll(generalNotifications);
        return usersNotifications;
    }

    @PatchMapping("/{id}")
    public void read(@PathVariable String id, @RequestHeader("X-AUTH-TOKEN-SUBJECT") String user) {
        log.info("Marking notification {} as read as user {}", id, user);
        notificationService.readNotification(new ObjectId(id), user);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<String> forbiddenOperationHandler() {
        return new ResponseEntity<>("Operation is not allowed", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementHandler() {
        return new ResponseEntity<>("Requested hub cannot be found", HttpStatus.NOT_FOUND);
    }

}
