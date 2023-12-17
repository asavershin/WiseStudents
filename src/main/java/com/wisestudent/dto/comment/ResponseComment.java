package com.wisestudent.dto.comment;

import com.wisestudent.dto.FileDto;
import com.wisestudent.dto.user.ResponseUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseComment {
    private Long id;
    private String text;
    private List<FileDto> files;
    private LocalDateTime createdAt;
    private Boolean isAnonymous;
    private ResponseUser user;
}
