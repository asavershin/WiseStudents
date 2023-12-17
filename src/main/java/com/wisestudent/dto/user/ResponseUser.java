package com.wisestudent.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wisestudent.models.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ResponseUser {
    private Long id;
    private String name;
    @JsonProperty("username")
    private String login;
    private String city;
    private Sex sex;
}
