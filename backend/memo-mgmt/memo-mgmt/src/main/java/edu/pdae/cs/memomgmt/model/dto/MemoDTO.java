package edu.pdae.cs.memomgmt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoDTO {

    private String author;
    private String id;
    private Date createdOn;

}
