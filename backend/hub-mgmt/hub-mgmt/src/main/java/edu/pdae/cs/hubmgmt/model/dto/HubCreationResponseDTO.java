package edu.pdae.cs.hubmgmt.model.dto;

import edu.pdae.cs.common.util.UserWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubCreationResponseDTO {

    private String id;
    private String name;
    private UserWrapper owner;
    private Date createdOn;

}
