package edu.pdae.cs.activitynotificationsmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import edu.pdae.cs.common.model.BaseEntity;
import edu.pdae.cs.common.model.Visibility;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("memos")
public class Memo extends BaseEntity {

    private String title;
    private String owner;
    private Visibility visibility;
    private Date dueDate;
    private Set<String> completions;
    private boolean archived;

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId hubId;

}
