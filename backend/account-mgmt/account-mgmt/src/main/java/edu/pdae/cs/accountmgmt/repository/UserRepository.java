package edu.pdae.cs.accountmgmt.repository;

import edu.pdae.cs.accountmgmt.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByEmail(String email);

}
