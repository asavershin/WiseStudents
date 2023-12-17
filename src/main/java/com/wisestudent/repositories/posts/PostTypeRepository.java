package com.wisestudent.repositories.posts;

import com.wisestudent.models.posts.PostTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTypeRepository extends JpaRepository<PostTypeEntity, Long> {
    boolean existsByName(String name);
}
