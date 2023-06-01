package edu.pdae.cs.common.model.dto;

import edu.pdae.cs.common.model.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFiredDTO {

    private Date date;
    private String hubName;
    private String hubId;
    private String memoTitle;
    private String memoId;
    private Type type;
    private String user;
    private Visibility visibility;

    public enum Type {
        MEMO_CREATED,
        HUB_CREATED,
        MEMO_COMPLETED,
    }

}
