package edu.pdae.cs.common.model.dto;

import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.model.Visibility;
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
public class ActivityFiredDTO {

    private Type activityType;
    private Visibility activityVisibility;
    private Date date;

    private String hubName;
    private String hubId;
    private String memoTitle;
    private String memoId;
    private UserWrapper takerUser;
    private Set<UserWrapper> affectedUsers;

}
