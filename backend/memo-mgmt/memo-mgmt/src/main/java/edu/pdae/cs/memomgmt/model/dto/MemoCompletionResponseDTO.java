package edu.pdae.cs.memomgmt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoCompletionResponseDTO {

    private String memoId;
    private String hubId;
    private String user;
    private boolean completed;

}
