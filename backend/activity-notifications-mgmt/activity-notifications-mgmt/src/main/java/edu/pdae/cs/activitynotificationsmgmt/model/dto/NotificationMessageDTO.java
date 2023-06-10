package edu.pdae.cs.activitynotificationsmgmt.model.dto;

import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.util.UserWrapper;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO {

    private UserWrapper takerUser;
    private Set<UserWrapper> affectedUsers;

    @Nullable
    private String hubId;
    @Nullable
    private String hubName;

    @Nullable
    private String memoId;
    @Nullable
    private String memoTitle;

    private Type type;

}
