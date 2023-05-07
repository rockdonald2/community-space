package edu.pdae.cs.memomgmt.service;

import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.MemoDTO;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface MemoService {

    List<MemoDTO> getAllByVisibility(Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAllAfter(Date after, String asUser);

    List<MemoDTO> getAllAfterAndByVisibility(Date after, Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAll(String asUser);

    List<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Memo.Visibility visibility, ObjectId hubId, String asUser);

    List<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser);

    List<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAllByHubId(ObjectId hubId, String asUser);

}
