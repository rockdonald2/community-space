package edu.pdae.cs.activitynotificationsmgmt.repository;

import edu.pdae.cs.activitynotificationsmgmt.model.Activity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, ObjectId> {

    List<Activity> findActivitiesByDateBetween(Date from, Date to);

}
