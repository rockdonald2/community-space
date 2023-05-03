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
public class HubDetailsDTO {

    private String id;
    private String name;
    private String description;
    private Date createdOn;
    private String owner;
    private String[] members;

}
