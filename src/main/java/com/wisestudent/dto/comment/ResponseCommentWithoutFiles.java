package com.wisestudent.dto.comment;

import com.wisestudent.dto.user.ResponseUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCommentWithoutFiles {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private Boolean isAnonymous;
    private ResponseUser user;
}
