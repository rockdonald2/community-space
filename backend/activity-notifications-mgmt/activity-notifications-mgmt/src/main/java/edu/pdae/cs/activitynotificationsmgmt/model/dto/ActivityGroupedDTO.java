package edu.pdae.cs.activitynotificationsmgmt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGroupedDTO {

    private int groupNumber;
    private long count;

}
