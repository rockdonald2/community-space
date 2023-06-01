package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import org.bson.types.ObjectId;

public interface MemoService {

    void createMemo(ObjectId memoId, String memoTitle, String ownerEmail, ObjectId hubId);

    void deleteMemo(ObjectId memoId);

    Memo getMemo(ObjectId memoId);

    void deleteAllByHubId(ObjectId hubId);

}
