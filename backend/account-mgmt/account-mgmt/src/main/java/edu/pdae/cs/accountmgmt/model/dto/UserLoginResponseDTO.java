package edu.pdae.cs.accountmgmt.model.dto;

import edu.pdae.cs.accountmgmt.model.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDTO {

    private Token token;
    private String email;
    private String firstName;
    private String lastName;

}
