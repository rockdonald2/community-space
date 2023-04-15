package edu.pdae.cs.accountmgmt.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPresenceNotificationDTO {

    private String email;
    private Status status;

    public enum Status {
        ONLINE,
        OFFLINE
    }

}
