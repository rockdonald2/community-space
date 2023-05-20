package edu.pdae.cs.common.model.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubMutationDTO {

    private String hubId;
    @Nullable
    private String owner;
    private State state;

    public enum State {
        CREATED,
        DELETED
    }

}
