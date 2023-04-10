package edu.pdae.cs.accountmgmt.model.dto;

import edu.pdae.cs.accountmgmt.model.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationResponseDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Token token;

}
