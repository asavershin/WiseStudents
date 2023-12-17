package com.wisestudent.repositories;

import com.wisestudent.models.DefaultCommentEntity;
import com.wisestudent.models.posts.PostCommentEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DefaultCommentRepository extends JpaRepository<DefaultCommentEntity, Long> {
    @Query("""
        SELECT c
        FROM DefaultCommentEntity c
        LEFT JOIN FETCH c.files
        WHERE c.createdAt < :date
    """)
    Slice<DefaultCommentEntity> getAllByCreatedAtBefore(LocalDateTime date, Pageable pageable);

    @Query("""
        SELECT c
        FROM DefaultCommentEntity c
        LEFT JOIN FETCH c.files
        LEFT JOIN FETCH c.user
        WHERE c.id = :commentId
    """)
    Optional<DefaultCommentEntity> findCommentByIdWithFilesAndUser(Long commentId);

    @Query("""
        SELECT c
        FROM DefaultCommentEntity c
        LEFT JOIN FETCH c.files
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH c.post
        WHERE c.id = :commentId
    """)
    Optional<DefaultCommentEntity> findCommentByIdWithFilesAndUserAndPost(Long commentId);
}
