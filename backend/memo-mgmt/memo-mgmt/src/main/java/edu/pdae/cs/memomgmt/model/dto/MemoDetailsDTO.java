package edu.pdae.cs.memomgmt.model.dto;

import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.util.UserWrapper;
import edu.pdae.cs.memomgmt.model.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoDetailsDTO {

    private String id;
    private String title;
    private UserWrapper author;
    private Date createdOn;
    private String content;
    private Visibility visibility;
    private Memo.Urgency urgency;
    private String hubId;
    private boolean completed;
    private Date dueDate;
    private boolean archived;
    private boolean pinned;

}
