package edu.pdae.cs.memomgmt.service;

import edu.pdae.cs.memomgmt.model.Memo;
import edu.pdae.cs.memomgmt.model.dto.MemoDTO;

import java.util.Date;
import java.util.List;

public interface MemoService {

    List<MemoDTO> getAllByVisibility(Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAllAfter(Date after, String asUser);

    List<MemoDTO> getAllAfterAndByVisibility(Date after, Memo.Visibility visibility, String asUser);

    List<MemoDTO> getAll(String asUser);

}
