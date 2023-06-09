package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.Memo;
import edu.pdae.cs.common.model.dto.MemoMutationDTO;
import org.bson.types.ObjectId;

import java.util.List;

public interface MemoService {

    void createMemo(MemoMutationDTO memoMutationDTO);

    void deleteMemo(ObjectId memoId);

    Memo getMemo(ObjectId memoId);

    void deleteAllByHubId(ObjectId hubId);

    void addCompletion(ObjectId memoId, String userEmail);

    void updateMemo(MemoMutationDTO memoMutationDTO);

    List<Memo> getDueMemos();

}
