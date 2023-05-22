package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.Activity;
import edu.pdae.cs.activitynotificationsmgmt.model.GroupBy;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityGroupedDTO;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface ActivityService {

    void addActivity(ObjectId hubId, Date date, Activity.Type type);

    List<ActivityDTO> getActivities(Date from, Date to);

    List<ActivityGroupedDTO> groupActivities(Date from, Date to, GroupBy groupBy);

}
