package com.wisestudent.repositories.news;

import com.wisestudent.models.news.ThreadCommentFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadCommentFileRepository extends JpaRepository<ThreadCommentFileEntity, Long> {
}
