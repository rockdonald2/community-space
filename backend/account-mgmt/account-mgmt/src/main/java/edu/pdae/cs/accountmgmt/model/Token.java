package edu.pdae.cs.accountmgmt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Field("_id")
    private ObjectId id;

    private String data;
    private boolean revoked;
    private boolean expired;

}
