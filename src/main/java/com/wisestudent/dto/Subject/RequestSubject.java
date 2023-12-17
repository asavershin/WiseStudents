package com.wisestudent.dto.Subject;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestSubject {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
