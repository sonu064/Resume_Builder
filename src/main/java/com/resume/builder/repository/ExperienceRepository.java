package com.resume.builder.repository;

import com.resume.builder.domain.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {}

