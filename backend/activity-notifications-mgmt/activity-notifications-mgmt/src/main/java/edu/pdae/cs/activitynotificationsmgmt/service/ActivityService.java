package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.GroupBy;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityGroupedDTO;
import edu.pdae.cs.common.model.dto.ActivityFiredDTO;
import edu.pdae.cs.common.util.PageWrapper;

import java.util.Date;
import java.util.List;

public interface ActivityService {

    void addActivity(ActivityFiredDTO activityFiredDTO);

    PageWrapper<ActivityDTO> getActivities(Date from, Date to, String asUser, int page, int pageSize);

    List<ActivityGroupedDTO> groupActivities(Date from, Date to, GroupBy groupBy);

}
