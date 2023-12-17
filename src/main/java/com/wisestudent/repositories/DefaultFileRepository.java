package com.wisestudent.repositories;

import com.wisestudent.models.DefaultFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultFileRepository extends JpaRepository<DefaultFileEntity, Long> {
}
