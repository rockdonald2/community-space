package edu.pdae.cs.accountmgmt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResponseDTO {

    private String email;

    @Builder.Default
    private List<String> authorities = new ArrayList<>();

}
