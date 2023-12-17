package com.wisestudent.dto.news;

import com.wisestudent.dto.FileDto;
import com.wisestudent.dto.user.ResponseUser;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseNews {
    private Long id;
    private String name;
    private String text;
    private LocalDateTime createdAt;
    private ResponseUser user;
    private List<FileDto> files;
}
