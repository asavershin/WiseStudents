package com.wisestudent.repositories.news;

import com.wisestudent.models.news.ThreadFileEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ThreadFileRepository extends JpaRepository<ThreadFileEntity, Long> {
}
