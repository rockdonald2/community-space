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
    private String firstName;
    private String lastName;

    public enum Status {
        ONLINE,
        OFFLINE
    }

}
