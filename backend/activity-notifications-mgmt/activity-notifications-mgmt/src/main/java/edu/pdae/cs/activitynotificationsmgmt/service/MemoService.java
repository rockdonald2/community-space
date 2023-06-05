package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import edu.pdae.cs.common.model.Visibility;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface MemoService {

    void createMemo(ObjectId memoId, String memoTitle, String ownerEmail, ObjectId hubId, Visibility visibility, Date dueDate);

    void deleteMemo(ObjectId memoId);

    Memo getMemo(ObjectId memoId);

    void deleteAllByHubId(ObjectId hubId);

    void addCompletion(ObjectId memoId, String userEmail);

    void updateMemo(ObjectId memoId, String memoTitle, Visibility visibility, Date dueDate);

    List<Memo> getDueMemos();

}
