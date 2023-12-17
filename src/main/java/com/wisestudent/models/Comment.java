package com.wisestudent.models;

import java.time.LocalDateTime;

public interface Comment {
    Long getId();
    void setId(Long id);
    String getText();
    void setText(String text);
    Boolean getIsAnonymous();
    void setIsAnonymous(Boolean isAnonymous);
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    void setUser(UserEntity user);
    UserEntity getUser();

}
