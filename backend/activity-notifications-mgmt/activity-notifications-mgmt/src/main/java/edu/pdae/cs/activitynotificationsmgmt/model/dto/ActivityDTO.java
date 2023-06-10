package edu.pdae.cs.activitynotificationsmgmt.model.dto;

import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.util.UserWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {

    private Date date;
    private Type type;

    private String hubId;
    private String hubName;
    private String memoId;
    private String memoTitle;
    private UserWrapper takerUser;
    private Set<UserWrapper> affectedUsers;

}
