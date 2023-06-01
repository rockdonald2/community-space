package edu.pdae.cs.activitynotificationsmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import edu.pdae.cs.common.model.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activities")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {

    @Id
    @Field("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private Date date;
    private ObjectId hubId;
    private String hubName;
    private ObjectId memoId;
    private String memoTitle;
    private Type type;
    private String user;
    private Visibility visibility;

    public enum Type {
        MEMO_CREATED,
        HUB_CREATED,
        MEMO_COMPLETED,
    }

}
