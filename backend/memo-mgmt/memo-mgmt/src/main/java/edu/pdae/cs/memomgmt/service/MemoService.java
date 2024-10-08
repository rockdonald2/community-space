package edu.pdae.cs.memomgmt.service;

import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.util.PageWrapper;
import edu.pdae.cs.common.util.UserWrapper;
import edu.pdae.cs.memomgmt.model.dto.*;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface MemoService {

    MemoCreationResponseDTO create(MemoCreationDTO memoCreationDTO, UserWrapper userWrapper);

    MemoCreationResponseDTO update(ObjectId id, MemoUpdateDTO memoUpdateDTO, String asUser);

    void delete(ObjectId id, String asUser);

    void deleteAllByHubId(ObjectId hubId);

    MemoDetailsDTO getById(ObjectId id, String asUser);

    PageWrapper<MemoDTO> getAllByVisibility(Visibility visibility, String asUser, int currPage, int pageSize, boolean archived);

    PageWrapper<MemoDTO> getAllAfter(Date after, String asUser, int currPage, int pageSize, boolean archived);

    PageWrapper<MemoDTO> getAllAfterAndByVisibility(Date after, Visibility visibility, String asUser, int currPage, int pageSize, boolean archived);

    PageWrapper<MemoDTO> getAll(String asUser, int currPage, int pageSize, boolean archived);

    PageWrapper<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Visibility visibility, ObjectId hubId, String asUser, int currPage, int pageSize, boolean archived);

    PageWrapper<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser, int currPage, int pageSize, boolean archived);

    PageWrapper<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Visibility visibility, String asUser, int currPage, int pageSize, boolean archived);

    PageWrapper<MemoDTO> getAllByHubId(ObjectId hubId, String asUser, int currPage, int pageSize, boolean archived);

    MemoCompletionResponseDTO completeMemo(ObjectId memoId, String user, UserWrapper userWrapper);

    List<MemoCompletionResponseDTO> getCompletions(ObjectId memoId, String asUser);

    MemoCompletionResponseDTO verifyCompletion(ObjectId memoId, String userToVerify, String asUser);

}
