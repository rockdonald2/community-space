package edu.pdae.cs.accountmgmt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
