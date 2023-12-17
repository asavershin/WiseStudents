package com.wisestudent.repositories.news;

import com.wisestudent.models.news.ThreadCommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadCommentRepository extends JpaRepository<ThreadCommentEntity, Long> {

    Slice<ThreadCommentEntity> findAllByParentCommentId(Long parentCommentId, Pageable pageable);
}
