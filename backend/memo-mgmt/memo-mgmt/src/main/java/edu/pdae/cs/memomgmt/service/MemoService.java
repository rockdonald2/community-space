package edu.pdae.cs.memomgmt.service;

import edu.pdae.cs.common.util.PageWrapper;
import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.*;
import org.bson.types.ObjectId;

import java.util.Date;

public interface MemoService {

    MemoCreationResponseDTO create(MemoCreationDTO memoCreationDTO, String asUser);

    MemoCreationResponseDTO update(ObjectId id, MemoUpdateDTO memoUpdateDTO, String asUser);

    void delete(ObjectId id, String asUser);

    void deleteAllByHubId(ObjectId hubId);

    MemoDetailsDTO getById(ObjectId id, String asUser);

    PageWrapper<MemoDTO> getAllByVisibility(Memo.Visibility visibility, String asUser, int currPage, int pageSize);

    PageWrapper<MemoDTO> getAllAfter(Date after, String asUser, int currPage, int pageSize);

    PageWrapper<MemoDTO> getAllAfterAndByVisibility(Date after, Memo.Visibility visibility, String asUser, int currPage, int pageSize);

    PageWrapper<MemoDTO> getAll(String asUser, int currPage, int pageSize);

    PageWrapper<MemoDTO> getAllAfterByHubIdAndByVisibility(Date after, Memo.Visibility visibility, ObjectId hubId, String asUser, int currPage, int pageSize);

    PageWrapper<MemoDTO> getAllAfterByHubId(Date after, ObjectId hubId, String asUser, int currPage, int pageSize);

    PageWrapper<MemoDTO> getAllByHubIdAndByVisibility(ObjectId hubId, Memo.Visibility visibility, String asUser, int currPage, int pageSize);

    PageWrapper<MemoDTO> getAllByHubId(ObjectId hubId, String asUser, int currPage, int pageSize);

}
