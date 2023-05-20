package edu.pdae.cs.memomgmt.service;

import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface MemoService {

    MemoCreationResponseDTO create(MemoCreationDTO memoCreationDTO, String asUser);

    MemoCreationResponseDTO update(ObjectId id, MemoUpdateDTO memoUpdateDTO, String asUser);

    void delete(ObjectId id, String asUser);

    void deleteAllByHubId(ObjectId hubId);

    MemoDetailsDTO getById(ObjectId id, String asUser);

    List<MemoDTO> getAllByVisibility(Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAllAfter(Date after, String asUser);

    List<MemoDTO> getAllAfterAndByVisibility(Date after, Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAll(String asUser);

    List<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Memo.Visibility visibility, ObjectId hubId, String asUser);

    List<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser);

    List<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAllByHubId(ObjectId hubId, String asUser);

}
