package com.wisestudent.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestPost {
    @NotBlank
    private String name;
    @NotNull
    @Size(min = 1, max = 6)
    private Integer year;
    @NotNull
    @Size(min = 1, max = 2)
    private Integer semester;
    @NotBlank
    private String text;
    @NotNull
    @Min(1)
    private Long subjectId;
    @NotNull
    @Min(1)
    private Long postTypeId;
    @NotNull
    @Min(1)
    private Long userId;
}
