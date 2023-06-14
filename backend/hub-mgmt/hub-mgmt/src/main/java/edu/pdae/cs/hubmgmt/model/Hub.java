package edu.pdae.cs.hubmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.pdae.cs.common.model.BaseEntity;
import edu.pdae.cs.common.util.UserWrapper;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hubs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hub extends BaseEntity {

    @Indexed(unique = true)
    private String name;

    private String description;
    private Date createdOn;

    private UserWrapper owner;

    @Builder.Default
    private Set<UserWrapper> members = new HashSet<>();
    @Builder.Default
    private Set<UserWrapper> waiting = new HashSet<>();

}
