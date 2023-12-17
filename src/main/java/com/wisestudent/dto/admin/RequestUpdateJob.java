package com.wisestudent.dto.admin;

import com.wisestudent.exceptions.constraints.CronFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestUpdateJob {
    @NotBlank
    @CronFormat
    private String cron;
    @NotNull
    private Long minusMinutes;
}
