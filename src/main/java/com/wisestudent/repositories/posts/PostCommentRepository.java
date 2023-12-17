package com.wisestudent.repositories.posts;

import com.wisestudent.models.posts.PostCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
    @Query("SELECT c " +
            "FROM PostCommentEntity c " +
            "LEFT JOIN FETCH c.files f " +
            "WHERE c.post.id = :postId")
    Page<PostCommentEntity> findAllByPostId(Long postId, Pageable pageable);
}
