package edu.pdae.cs.common.model.dto;

import edu.pdae.cs.common.model.Visibility;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoMutationDTO {

    private String memoId;
    private String hubId;

    @Nullable
    private String title;

    @Nullable
    private String owner;

    @Nullable
    private Date dueDate;
    @Nullable
    private Visibility visibility;

    private State state;

    public enum State {
        CREATED,
        DELETED
    }

}
