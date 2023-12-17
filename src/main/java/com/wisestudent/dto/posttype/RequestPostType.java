package com.wisestudent.dto.posttype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestPostType {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
