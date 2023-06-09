package edu.pdae.cs.memomgmt.repository;

import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.memomgmt.model.Memo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MemoRepository extends MongoRepository<Memo, ObjectId> {

    Page<Memo> getMemosByCreatedOnBefore(Date date, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfter(Date date, Pageable pageable);

    Page<Memo> getMemosByAuthor(String author, Pageable pageable);

    Page<Memo> getMemosByVisibility(Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByUrgency(Memo.Urgency urgency, Pageable pageable);

    Page<Memo> getMemosByAuthorAndVisibility(String author, Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndVisibility(Date date, Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndVisibilityAndAuthor(Date date, Visibility visibility, String author, Pageable pageable);

    Page<Memo> getMemosByHubId(ObjectId hubId, Pageable pageable);

    Page<Memo> getMemosByHubIdAndVisibility(ObjectId hubId, Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndHubId(Date date, ObjectId hubId, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndHubIdAndVisibility(Date date, ObjectId hubId, Visibility visibility, Pageable pageable);

    Page<Memo> getMemosByCreatedOnBeforeAndArchived(Date date, boolean archived, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndArchived(Date date, boolean archived, Pageable pageable);

    Page<Memo> getMemosByAuthorAndArchived(String author, boolean archived, Pageable pageable);

    Page<Memo> getMemosByVisibilityAndArchived(Visibility visibility, boolean archived, Pageable pageable);

    Page<Memo> getMemosByUrgencyAndArchived(Memo.Urgency urgency, boolean archived, Pageable pageable);

    Page<Memo> getMemosByAuthorAndVisibilityAndArchived(String author, Visibility visibility, boolean archived, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndVisibilityAndArchived(Date date, Visibility visibility, boolean archived, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndVisibilityAndAuthorAndArchived(Date date, Visibility visibility, String author, boolean archived, Pageable pageable);

    Page<Memo> getMemosByHubIdAndArchived(ObjectId hubId, boolean archived, Pageable pageable);

    Page<Memo> getMemosByHubIdAndVisibilityAndArchived(ObjectId hubId, Visibility visibility, boolean archived, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndHubIdAndArchived(Date date, ObjectId hubId, boolean archived, Pageable pageable);

    Page<Memo> getMemosByCreatedOnAfterAndHubIdAndVisibilityAndArchived(Date date, ObjectId hubId, Visibility visibility, boolean archived, Pageable pageable);

    Page<Memo> getMemosByArchived(boolean archived, Pageable pageable);

    void deleteAllByHubId(ObjectId hubId);

}
