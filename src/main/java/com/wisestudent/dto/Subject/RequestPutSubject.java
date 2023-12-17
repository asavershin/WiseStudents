package com.wisestudent.dto.Subject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestPutSubject {
    @NotNull
    @Min(1)
    private Long id;
    @NotBlank
    @Size(min = 1,max = 50)
    private String name;
}
