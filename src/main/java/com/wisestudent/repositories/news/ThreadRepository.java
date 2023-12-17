package com.wisestudent.repositories.news;

import com.wisestudent.models.news.ThreadEntity;
import com.wisestudent.models.news.ThreadWithChildCommentsCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ThreadRepository extends JpaRepository<ThreadEntity, Long> {
    @Query("""
            SELECT t as threadEntity
            FROM ThreadEntity t
            JOIN FETCH t.user
            LEFT JOIN FETCH t.files
            WHERE t.news.id = :newsId
    """)
    Slice<ThreadEntity> findThreadsWithChildCommentsCountByNewsId(Long newsId, Pageable pageable);
}
