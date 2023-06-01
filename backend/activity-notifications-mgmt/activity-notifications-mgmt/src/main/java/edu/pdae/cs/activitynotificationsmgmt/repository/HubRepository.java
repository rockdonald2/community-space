package edu.pdae.cs.activitynotificationsmgmt.repository;

import edu.pdae.cs.activitynotificationsmgmt.model.Hub;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HubRepository extends MongoRepository<Hub, ObjectId> {

    public List<Hub> findAllByMembersContains(String email);

}
