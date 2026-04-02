package com.resume.builder.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "projects", indexes = @Index(name = "idx_projects_resume_id", columnList = "resume_id"))
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "resume_id", nullable = false)
  private Resume resume;

  @Column(nullable = false)
  private int position;

  @Column(nullable = false, length = 180)
  private String name;

  @Column(length = 220)
  private String url;

  @Column(length = 2000)
  private String description;

  @Column(length = 500)
  private String techStack;

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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTechStack() {
    return techStack;
  }

  public void setTechStack(String techStack) {
    this.techStack = techStack;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}

