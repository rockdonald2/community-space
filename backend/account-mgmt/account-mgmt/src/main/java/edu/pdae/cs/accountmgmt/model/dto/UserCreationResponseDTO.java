package edu.pdae.cs.accountmgmt.model.dto;

import edu.pdae.cs.accountmgmt.model.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationResponseDTO {

    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private Token token;

}
