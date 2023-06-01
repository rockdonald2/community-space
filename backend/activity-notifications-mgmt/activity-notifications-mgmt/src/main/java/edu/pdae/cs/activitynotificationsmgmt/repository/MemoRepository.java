package edu.pdae.cs.activitynotificationsmgmt.repository;

import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends MongoRepository<Memo, ObjectId> {

    void deleteAllByHubId(ObjectId hubId);

}
