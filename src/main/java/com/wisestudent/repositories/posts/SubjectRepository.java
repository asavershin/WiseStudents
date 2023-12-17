package com.wisestudent.repositories.posts;

import com.wisestudent.models.posts.SubjectEntity;
import com.wisestudent.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    boolean existsByName(String name);

    @Query("""
        SELECT f
        FROM SubjectEntity s
        LEFT JOIN s.posts p
        LEFT JOIN p.files f
        WHERE s.id = :id
    """)
    Set<File> findAllFilesInPosts(Long id);

    @Query("""
        SELECT f
        FROM SubjectEntity s
        LEFT JOIN s.posts p
        LEFT JOIN p.postComments c
        LEFT JOIN c.files f
        WHERE s.id = :id
    """)
    Set<File> findAllFilesInComments(Long id);
}
