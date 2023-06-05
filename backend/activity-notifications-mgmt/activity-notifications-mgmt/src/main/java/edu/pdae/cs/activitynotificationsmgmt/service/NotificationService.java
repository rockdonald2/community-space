package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.Notification;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.DueMemoDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.NotificationMessageDTO;
import edu.pdae.cs.common.model.dto.NotificationDTO;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface NotificationService {

    void readNotification(ObjectId notificationId, String user);

    Notification addNotification(String target, Notification.TargetType targetType, String msg, String taker);

    void handleNotification(NotificationMessageDTO notificationMessageDTO);

    void broadcastNotification(String target, NotificationDTO notificationDTO);

    List<NotificationDTO> getNotifications(String target, String asUser, Date from, Date to);

    void queryDueMemos();

    void handleDueMemo(DueMemoDTO dueMemoDTO);

}
