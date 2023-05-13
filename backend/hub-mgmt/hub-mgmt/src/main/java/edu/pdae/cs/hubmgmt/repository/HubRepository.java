package edu.pdae.cs.hubmgmt.repository;

import edu.pdae.cs.hubmgmt.model.Hub;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HubRepository extends MongoRepository<Hub, ObjectId> {

    Optional<Hub> findByName(String name);

}
