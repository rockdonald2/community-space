package edu.pdae.cs.memomgmt.model.dto;

import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.memomgmt.model.Memo;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoUpdateDTO {

    @Nullable
    private String content;
    @Nullable
    private Visibility visibility;
    @Nullable
    private Memo.Urgency urgency;
    @Nullable
    private String title;
    @Nullable
    private Date dueDate;
    @Nullable
    private Boolean archived;

}
