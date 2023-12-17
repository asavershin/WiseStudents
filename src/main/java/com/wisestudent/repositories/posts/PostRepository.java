package com.wisestudent.repositories.posts;

import com.wisestudent.models.posts.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query("""
        SELECT p
        FROM PostEntity p
        LEFT JOIN FETCH p.files
        LEFT JOIN FETCH p.user
        WHERE p.id = :postId
    """)
    Optional<PostEntity> findPostByIdWithFiles(Long postId);

    @Query("""
        SELECT p
        FROM PostEntity p
        LEFT JOIN FETCH p.files
        LEFT JOIN FETCH p.postComments
        WHERE p.id = :postId
    """)
    Optional<PostEntity> findPostByIdWithFilesAndUserAndComments(Long postId);
}
