package edu.pdae.cs.activitynotificationsmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.pdae.cs.common.model.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("notifications")
public class Notification extends BaseEntity {

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
