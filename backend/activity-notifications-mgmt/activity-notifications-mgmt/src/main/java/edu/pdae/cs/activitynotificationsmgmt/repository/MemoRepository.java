package edu.pdae.cs.activitynotificationsmgmt.repository;

import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import edu.pdae.cs.common.model.Visibility;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MemoRepository extends MongoRepository<Memo, ObjectId> {

    void deleteAllByHubId(ObjectId hubId);

    List<Memo> findAllByDueDateBetweenAndVisibility(Date from, Date to, Visibility visibility);

}
