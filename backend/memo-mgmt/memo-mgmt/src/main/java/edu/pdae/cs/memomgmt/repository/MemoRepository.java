package edu.pdae.cs.memomgmt.repository;

import edu.pdae.cs.memomgmt.model.Memo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface MemoRepository extends MongoRepository<Memo, ObjectId> {

    List<Memo> getMemosByCreatedOnBefore(Date date);

    List<Memo> getMemosByCreatedOnAfter(Date date);

    List<Memo> getMemosByAuthor(String author);

    List<Memo> getMemosByVisibility(Memo.Visibility visibility);

    List<Memo> getMemosByUrgency(Memo.Urgency urgency);

    List<Memo> getMemosByAuthorAndVisibility(String author, Memo.Visibility visibility);

    List<Memo> getMemosByCreatedOnAfterAndVisibility(Date date, Memo.Visibility visibility);

    List<Memo> getMemosByCreatedOnAfterAndVisibilityAndAuthor(Date date, Memo.Visibility visibility, String author);

    List<Memo> getMemosByHubId(ObjectId hubId);

    List<Memo> getMemosByHubIdAndVisibility(ObjectId hubId, Memo.Visibility visibility);

    List<Memo> getMemosByCreatedOnAfterAndHubId(Date date, ObjectId hubId);

    List<Memo> getMemosByCreatedOnAfterAndHubIdAndVisibility(Date date, ObjectId hubId, Memo.Visibility visibility);

}
