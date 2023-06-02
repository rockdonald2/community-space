package edu.pdae.cs.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMutationDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;

    private State state;

    public enum State {
        ADDED,
        REMOVED
    }

}
