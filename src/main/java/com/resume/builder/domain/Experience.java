package com.resume.builder.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "experience", indexes = @Index(name = "idx_experience_resume_id", columnList = "resume_id"))
public class Experience {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "resume_id", nullable = false)
  private Resume resume;

  @Column(nullable = false)
  private int position;

  @Column(nullable = false, length = 180)
  private String role;

  @Column(nullable = false, length = 180)
  private String company;

  @Column(length = 2000)
  private String description;

  private LocalDate startDate;
  private LocalDate endDate;

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

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}

