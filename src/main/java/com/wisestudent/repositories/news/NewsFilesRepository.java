package com.wisestudent.repositories.news;

import com.wisestudent.models.news.NewsFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsFilesRepository extends JpaRepository<NewsFileEntity, Long> {
}
