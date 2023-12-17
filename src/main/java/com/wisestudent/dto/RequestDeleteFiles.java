package com.wisestudent.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class RequestDeleteFiles {
    //userId нужно заменить, на получение userId с помощью springSecurity через authentication
    //внутри метода контроллера, так надёжнее
    @NotNull
    @Min(1)
    private Long userId;
    @NotNull
    @Min(1)
    private Long id;
    @Size(min = 1)
    private List<Long> filesids;
}
