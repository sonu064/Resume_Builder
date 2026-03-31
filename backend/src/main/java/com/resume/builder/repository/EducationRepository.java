package com.resume.builder.repository;

import com.resume.builder.domain.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long> {}

