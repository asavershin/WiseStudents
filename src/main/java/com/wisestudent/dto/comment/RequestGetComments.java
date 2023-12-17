package com.wisestudent.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wisestudent.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestGetComments extends PageDto{
    @NotNull
    private Long postId;
}
