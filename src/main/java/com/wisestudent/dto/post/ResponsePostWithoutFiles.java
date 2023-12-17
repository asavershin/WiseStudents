package com.wisestudent.dto.post;

import com.wisestudent.dto.Subject.ResponseSubject;
import com.wisestudent.dto.posttype.ResponsePostType;
import com.wisestudent.dto.user.ResponseUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponsePostWithoutFiles {
    private Long id;
    private String name;
    private ResponseSubject subject;
    private Long semester;
    private ResponsePostType postType;
    private String text;
    private Long year;
    private LocalDateTime createdAt;
    private ResponseUser user;
}
