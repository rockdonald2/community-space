package edu.pdae.cs.activitynotificationsmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("notifications")
public class Notification {

    @Id
    @Field("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String owner; // target
    private String taker; // user
    private String msg;
    private Set<String> reads;
    private Date createdAt;
    private TargetType targetType;

    public enum TargetType {
        GROUP,
        USER
    }

    @Getter
    @AllArgsConstructor
    public enum GroupTargets {
        GENERAL("general");

        private final String value;
    }

}
