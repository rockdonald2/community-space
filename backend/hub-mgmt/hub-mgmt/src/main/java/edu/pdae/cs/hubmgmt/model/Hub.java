package edu.pdae.cs.hubmgmt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hubs")
public class Hub {

    @Id
    @Field("_id")
    private ObjectId id;

    @Indexed(unique = true)
    private String name;

    private String description;
    private Date createdOn;

    private String owner;

    @Builder.Default
    private List<String> members = new ArrayList<>();
    @Builder.Default
    private List<String> waiting = new ArrayList<>();

}
