package com.wisestudent.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wisestudent.exceptions.constraints.SexConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RequestUser {
    @NotNull
    @Length(min = 1, max = 50)
    private String name;

    @NotNull
    @JsonProperty(value = "username")
    @Length(min = 1, max = 50)
    private String login;

    @NotNull
    @Length(min = 1, max = 50)
    private String password;

    @Length(min = 1, max = 50)
    private String city;

    @SexConstraint
    private String sex;
}
