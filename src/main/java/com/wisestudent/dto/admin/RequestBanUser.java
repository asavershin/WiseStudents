package com.wisestudent.dto.admin;

import com.wisestudent.exceptions.constraints.LocalDateFormat;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RequestBanUser {
    @Min(1)
    private Long userId;
    @LocalDateFormat
    private String date;
}
