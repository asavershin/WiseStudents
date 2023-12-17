package com.wisestudent.repositories.posts;

import com.wisestudent.models.posts.PostFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFileRepository extends JpaRepository<PostFileEntity, Long> {
}
