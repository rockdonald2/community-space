package edu.pdae.cs.hubmgmt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubDTO {

    private String id;
    private String name;
    private Date createdOn;
    private String owner;
    private Role role;

    public enum Role {
        OWNER,
        MEMBER,
        WAITING,
        NONE
    }

}
