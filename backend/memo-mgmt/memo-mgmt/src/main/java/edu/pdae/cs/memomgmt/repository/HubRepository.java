package edu.pdae.cs.memomgmt.repository;

import edu.pdae.cs.memomgmt.model.Hub;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HubRepository extends MongoRepository<Hub, ObjectId> {
}
