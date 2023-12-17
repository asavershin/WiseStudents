package com.wisestudent.dto.post;

import com.wisestudent.dto.FileDto;
import com.wisestudent.dto.Subject.ResponseSubject;
import com.wisestudent.dto.posttype.ResponsePostType;
import com.wisestudent.dto.user.ResponseUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponsePost {
    private Long id;
    private String name;
    private ResponseSubject subject;
    private Long semester;
    private ResponsePostType postType;
    private String text;
    private Long year;
    private List<FileDto> files;
    private LocalDateTime createdAt;
    private ResponseUser user;
}
