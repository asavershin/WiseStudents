package com.wisestudent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("total_size")
    private long totalSize;

    @JsonProperty("page_number")
    private int pageNumber;

    @JsonProperty("page_size")
    private int pageSize;

    private List<T> content;
}
