package edu.pdae.cs.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubMemberMutationDTO {

    private String hubId;
    private String email;
    private State state;

    public enum State {
        ADDED,
        REMOVED
    }

}
