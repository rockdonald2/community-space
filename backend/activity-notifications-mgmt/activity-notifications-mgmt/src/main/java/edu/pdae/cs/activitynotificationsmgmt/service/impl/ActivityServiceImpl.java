package edu.pdae.cs.activitynotificationsmgmt.service.impl;

import edu.pdae.cs.activitynotificationsmgmt.model.Activity;
import edu.pdae.cs.activitynotificationsmgmt.model.GroupBy;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.ActivityGroupedDTO;
import edu.pdae.cs.activitynotificationsmgmt.repository.ActivityRepository;
import edu.pdae.cs.activitynotificationsmgmt.service.ActivityService;
import edu.pdae.cs.common.util.PageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ModelMapper modelMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public void addActivity(String user, ObjectId hubId, String hubName, Date date, Activity.Type type) {
        activityRepository.save(Activity.builder().user(user).hubId(hubId).date(date).type(type).hubName(hubName).build());
    }

    @Override
    @CacheEvict({"activities", "activities-grouped"})
    public void addActivity(String user, ObjectId hubId, String hubName, ObjectId memoId, String memoTitle, Date date, Activity.Type type) {
        activityRepository.save(Activity.builder().user(user).hubId(hubId).date(date).type(type).hubName(hubName).memoId(memoId).memoTitle(memoTitle).build());
    }

    @Override
    @Cacheable("activities")
    public PageWrapper<ActivityDTO> getActivities(Date from, Date to, int page, int pageSize) {
        final var activityPage = activityRepository.findActivitiesByDateBetween(from, to, PageRequest.of(page, pageSize));
        final List<Activity> activityList = activityPage.getContent();
        final List<ActivityDTO> activityDTOList = activityList.stream().map(activity -> modelMapper.map(activity, ActivityDTO.class)).toList();

        return PageWrapper.<ActivityDTO>builder()
                .pageSize(pageSize)
                .content(activityDTOList)
                .totalNumberOfElements(activityPage.getTotalElements())
                .totalPages(activityPage.getTotalPages())
                .build();
    }

    @Override
    @Cacheable("activities-grouped")
    public List<ActivityGroupedDTO> groupActivities(Date from, Date to, GroupBy groupBy) {
        if (groupBy.equals(GroupBy.DAY)) {
            final var filterByDate = Aggregation.match(Criteria.where("date").gte(from).lt(to));
            final var dayNumberProjection = Aggregation.project("date").andExpression("dayOfMonth(date)").as("day");
            final var countGroup = Aggregation.group("day").count().as("count");
            final var renameIdProjection = Aggregation.project("_id", "count").andExclude("_id").and("_id").as("groupNumber");
            return mongoTemplate.aggregate(Aggregation.newAggregation(filterByDate, dayNumberProjection, countGroup, renameIdProjection), mongoTemplate.getCollectionName(Activity.class), ActivityGroupedDTO.class).getMappedResults();
        }

        return Collections.emptyList();
    }

}
