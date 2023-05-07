package edu.pdae.cs.memomgmt.model.dto;

import edu.pdae.cs.memomgmt.model.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoCreationDTO {

    private String title;
    private String author;
    private String content;
    private Memo.Visibility visibility;
    private Memo.Urgency urgency;
    private ObjectId hubId;

}
