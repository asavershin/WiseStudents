package com.wisestudent.repositories.news;

import com.wisestudent.models.news.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
}
