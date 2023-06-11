package edu.pdae.cs.hubmgmt.model.dto;

import edu.pdae.cs.common.util.UserWrapper;
import edu.pdae.cs.hubmgmt.model.Role;
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
    private UserWrapper owner;
    private Role role;

}
