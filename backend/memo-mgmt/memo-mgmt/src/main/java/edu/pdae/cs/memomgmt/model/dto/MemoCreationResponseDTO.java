package edu.pdae.cs.memomgmt.model.dto;

import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.util.UserWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoCreationResponseDTO {

    private String id;
    private String title;
    private UserWrapper author;
    private Date createdOn;
    private String hubId;
    private Date dueDate;
    private Visibility visibility;
    private boolean isArchived;

}
