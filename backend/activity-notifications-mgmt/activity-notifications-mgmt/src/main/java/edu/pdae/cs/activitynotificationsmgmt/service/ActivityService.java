package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.GroupBy;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityGroupedDTO;
import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.util.PageWrapper;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface ActivityService {

    void addActivity(String user, String userName, ObjectId hubId, String hubName, Date date, Type type, Visibility visibility);

    void addActivity(String user, String userName, ObjectId hubId, String hubName, ObjectId memoId, String memoTitle, Date date, Type type, Visibility visibility);

    PageWrapper<ActivityDTO> getActivities(Date from, Date to, String asUser, int page, int pageSize);

    List<ActivityGroupedDTO> groupActivities(Date from, Date to, GroupBy groupBy);

}
