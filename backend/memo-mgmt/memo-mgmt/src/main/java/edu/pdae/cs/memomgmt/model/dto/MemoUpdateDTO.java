package edu.pdae.cs.memomgmt.model.dto;

import edu.pdae.cs.memomgmt.model.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoUpdateDTO {

    private ObjectId id;

    private Optional<String> content;
    private Optional<Memo.Visibility> visibility;

}
