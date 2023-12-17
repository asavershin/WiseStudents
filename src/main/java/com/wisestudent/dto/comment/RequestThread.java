package com.wisestudent.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestThread {
    @NotNull
    private Long newsId;
    @NotBlank
    private String text;
    @NotBlank
    private String isAnonymous;
}
