package edu.pdae.cs.memomgmt.repository;

import edu.pdae.cs.memomgmt.model.Memo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MemoRepository extends MongoRepository<Memo, ObjectId> {

    Page<Memo> getMemosByCreatedOnBefore(Date date, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfter(Date date, Pageable pageable);

    Page<Memo> getMemosByAuthor(String author, Pageable pageable);

    Page<Memo> getMemosByVisibility(Memo.Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByUrgency(Memo.Urgency urgency, Pageable pageable);

    Page<Memo> getMemosByAuthorAndVisibility(String author, Memo.Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndVisibility(Date date, Memo.Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndVisibilityAndAuthor(Date date, Memo.Visibility visibility, String author, Pageable pageable);

    Page<Memo> getMemosByHubId(ObjectId hubId, Pageable pageable);

    Page<Memo> getMemosByHubIdAndVisibility(ObjectId hubId, Memo.Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndHubId(Date date, ObjectId hubId, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndHubIdAndVisibility(Date date, ObjectId hubId, Memo.Visibility visibility, Pageable pageable);

    void deleteAllByHubId(ObjectId hubId);

}
