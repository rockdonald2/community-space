package edu.pdae.cs.memomgmt.model.dto;

import edu.pdae.cs.memomgmt.model.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoCreationDTO {

    private String author;
    private String content;
    private Memo.Visibility visibility;

}
