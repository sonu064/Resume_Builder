package com.resume.builder.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "resumes",
    indexes = {
        @Index(name = "idx_resumes_user_id", columnList = "user_id"),
        @Index(name = "uk_resumes_share_token", columnList = "share_token")
    }
)
public class Resume {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 64)
  private String templateKey = "classic";

  @Column(nullable = false, length = 160)
  private String fullName;

  @Column(length = 220)
  private String headline;

  @Column(length = 220)
  private String email;

  @Column(length = 40)
  private String phone;

  @Column(length = 220)
  private String website;

  @Column(length = 220)
  private String location;

  @Column(length = 5000)
  private String summary;

  @Column(length = 500)
  private String profileImageUrl;

  @Column(length = 64)
  private String shareToken;

  private Instant shareTokenCreatedAt;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("position ASC")
  private List<Education> education = new ArrayList<>();

  @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("position ASC")
  private List<Experience> experience = new ArrayList<>();

  @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("position ASC")
  private List<Skill> skills = new ArrayList<>();

  @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("position ASC")
  private List<Project> projects = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getTemplateKey() {
    return templateKey;
  }

  public void setTemplateKey(String templateKey) {
    this.templateKey = templateKey;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public String getShareToken() {
    return shareToken;
  }

  public void setShareToken(String shareToken) {
    this.shareToken = shareToken;
  }

  public Instant getShareTokenCreatedAt() {
    return shareTokenCreatedAt;
  }

  public void setShareTokenCreatedAt(Instant shareTokenCreatedAt) {
    this.shareTokenCreatedAt = shareTokenCreatedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public List<Education> getEducation() {
    return education;
  }

  public void setEducation(List<Education> education) {
    this.education = education;
  }

  public List<Experience> getExperience() {
    return experience;
  }

  public void setExperience(List<Experience> experience) {
    this.experience = experience;
  }

  public List<Skill> getSkills() {
    return skills;
  }

  public void setSkills(List<Skill> skills) {
    this.skills = skills;
  }

  public List<Project> getProjects() {
    return projects;
  }

  public void setProjects(List<Project> projects) {
    this.projects = projects;
  }
}

