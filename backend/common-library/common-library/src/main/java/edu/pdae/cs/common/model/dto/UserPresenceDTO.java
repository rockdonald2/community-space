package edu.pdae.cs.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPresenceDTO implements Serializable { // for some reason redisson needs this to be serializable

    private String email;
    private Date lastSeen;

}
