package edu.pdae.cs.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageWrapper<T> {

    private long totalNumberOfElements;
    private int totalPages;
    private int pageSize;
    private List<T> content;

}
