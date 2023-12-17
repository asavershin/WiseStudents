package com.wisestudent.repositories.posts;

import com.wisestudent.models.posts.PostCommentFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentFileRepository extends JpaRepository<PostCommentFileEntity, Long> {
}
