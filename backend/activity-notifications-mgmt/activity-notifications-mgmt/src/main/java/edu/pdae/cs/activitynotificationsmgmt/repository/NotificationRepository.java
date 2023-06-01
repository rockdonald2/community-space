package edu.pdae.cs.activitynotificationsmgmt.repository;

import edu.pdae.cs.activitynotificationsmgmt.model.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {


    Page<Notification> findAllByOwnerAndReadsNotContainsAndCreatedAtBetweenOrderByCreatedAtDesc(String owner, String reader, Date from, Date to, Pageable pageable);

}
