package edu.pdae.cs.activitynotificationsmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import edu.pdae.cs.common.model.BaseEntity;
import edu.pdae.cs.common.model.Type;
import edu.pdae.cs.common.model.Visibility;
import edu.pdae.cs.common.util.UserWrapper;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activities")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity extends BaseEntity {

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
