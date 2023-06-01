package edu.pdae.cs.memomgmt.model.dto;

import edu.pdae.cs.memomgmt.model.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import edu.pdae.cs.common.model.Visibility;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoCreationDTO {

    private String title;
    private String content;
    private Visibility visibility;
    private Memo.Urgency urgency;
    private ObjectId hubId;

}
