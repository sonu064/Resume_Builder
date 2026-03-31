package com.resume.builder.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class ResumeDtos {

  public record ResumeSummaryResponse(
      Long id,
      String fullName,
      String templateKey,
      Instant updatedAt
  ) {}

  public record EducationDto(
      Long id,
      int position,
      String degree,
      String school,
      String location,
      LocalDate startDate,
      LocalDate endDate,
      String description
  ) {}

  public record ExperienceDto(
      Long id,
      int position,
      String role,
      String company,
      String description,
      LocalDate startDate,
      LocalDate endDate
  ) {}

  public record SkillDto(
      Long id,
      int position,
      String name,
      String level
  ) {}

  public record ProjectDto(
      Long id,
      int position,
      String name,
      String url,
      String description,
      String techStack
  ) {}

  public record ResumeDetailsResponse(
      Long id,
      String templateKey,
      String fullName,
      String headline,
      String email,
      String phone,
      String website,
      String location,
      String summary,
      String profileImageUrl,
      String shareToken,
      List<EducationDto> education,
      List<ExperienceDto> experience,
      List<SkillDto> skills,
      List<ProjectDto> projects,
      Instant createdAt,
      Instant updatedAt
  ) {}

  public record CreateResumeRequest(
      @NotBlank String fullName,
      String headline,
      @Email String email,
      String phone,
      String website,
      String location,
      @Size(max = 5000) String summary,
      @Size(max = 64) String templateKey
  ) {}

  public record ResumeBulkUpdateRequest(
      @NotBlank String fullName,
      String headline,
      @Email String email,
      String phone,
      String website,
      String location,
      @Size(max = 5000) String summary,
      @Size(max = 64) String templateKey,
      @Valid @NotNull List<EducationItemRequest> education,
      @Valid @NotNull List<ExperienceItemRequest> experience,
      @Valid @NotNull List<SkillItemRequest> skills,
      @Valid @NotNull List<ProjectItemRequest> projects
  ) {}

  public record EducationItemRequest(
      Integer position,
      @NotBlank @Size(max = 180) String degree,
      @NotBlank @Size(max = 180) String school,
      @Size(max = 180) String location,
      LocalDate startDate,
      LocalDate endDate,
      @Size(max = 2000) String description
  ) {}

  public record EducationListRequest(
      @Valid @NotNull List<EducationItemRequest> items
  ) {}

  public record ExperienceItemRequest(
      Integer position,
      @NotBlank @Size(max = 180) String role,
      @NotBlank @Size(max = 180) String company,
      @Size(max = 2000) String description,
      LocalDate startDate,
      LocalDate endDate
  ) {}

  public record ExperienceListRequest(
      @Valid @NotNull List<ExperienceItemRequest> items
  ) {}

  public record SkillItemRequest(
      Integer position,
      @NotBlank @Size(max = 120) String name,
      @Size(max = 40) String level
  ) {}

  public record SkillListRequest(
      @Valid @NotNull List<SkillItemRequest> items
  ) {}

  public record ProjectItemRequest(
      Integer position,
      @NotBlank @Size(max = 180) String name,
      @Size(max = 220) String url,
      @Size(max = 2000) String description,
      @Size(max = 500) String techStack
  ) {}

  public record ProjectListRequest(
      @Valid @NotNull List<ProjectItemRequest> items
  ) {}

  public record ResumeScoreResponse(
      int score,
      List<String> suggestions
  ) {}

  public record ShareResponse(
      String shareUrl
  ) {}
}

