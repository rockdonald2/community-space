package edu.pdae.cs.activitynotificationsmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.util.UserWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Set;

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

    private UserWrapper takerUser;
    private Set<UserWrapper> affectedUsers;

    private Visibility activityVisibility;
    private Date date;
    private Type activityType;

    private ObjectId hubId;
    private String hubName;
    private ObjectId memoId;
    private String memoTitle;

}
