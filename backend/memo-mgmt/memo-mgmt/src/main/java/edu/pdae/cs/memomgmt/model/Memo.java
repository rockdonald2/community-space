package edu.pdae.cs.memomgmt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Memo {

    @Id
    @Field("_id")
    private ObjectId id;

    private String title;
    private String author;
    private String content;
    private Date createdOn;
    private Visibility visibility;
    private Urgency urgency;
    private ObjectId hubId;

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }

    public enum Urgency {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

}
