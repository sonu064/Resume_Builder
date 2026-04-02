package com.resume.builder.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "skills", indexes = @Index(name = "idx_skills_resume_id", columnList = "resume_id"))
public class Skill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "resume_id", nullable = false)
  private Resume resume;

  @Column(nullable = false)
  private int position;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(length = 40)
  private String level;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;

  public Long getId() {
    return id;
  }

  public Resume getResume() {
    return resume;
  }

  public void setResume(Resume resume) {
    this.resume = resume;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}

