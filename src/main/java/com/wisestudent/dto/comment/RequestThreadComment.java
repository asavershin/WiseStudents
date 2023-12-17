package com.wisestudent.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestThreadComment {
    @NotNull
    private Long threadId;
    @NotBlank
    private String text;
    @NotBlank
    private String isAnonymous;
}
