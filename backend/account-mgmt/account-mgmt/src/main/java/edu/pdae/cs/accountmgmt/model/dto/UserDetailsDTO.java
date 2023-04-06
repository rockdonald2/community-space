package edu.pdae.cs.accountmgmt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;

}
