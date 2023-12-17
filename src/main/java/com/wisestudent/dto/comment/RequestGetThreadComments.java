package com.wisestudent.dto.comment;

import com.wisestudent.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestGetThreadComments extends PageDto {
    @NotNull
    private Long threadId;
}
