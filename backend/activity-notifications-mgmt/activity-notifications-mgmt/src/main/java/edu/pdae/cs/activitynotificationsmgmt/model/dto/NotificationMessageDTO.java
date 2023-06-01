package edu.pdae.cs.activitynotificationsmgmt.model.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO {

    private String user;

    @Nullable
    private String hubId;
    @Nullable
    private String hubName;

    @Nullable
    private String memoId;
    @Nullable
    private String memoTitle;

    private Type type;

    public enum Type {
        MEMO_CREATED,
        HUB_CREATED,
        MEMO_COMPLETED,
    }

}
