package com.resume.builder.api.controller;

import com.resume.builder.api.dto.ResumeDtos;
import com.resume.builder.security.SecurityContextUtil;
import com.resume.builder.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

  private final ResumeService resumeService;
  private final SecurityContextUtil securityContextUtil;

  public ResumeController(ResumeService resumeService, SecurityContextUtil securityContextUtil) {
    this.resumeService = resumeService;
    this.securityContextUtil = securityContextUtil;
  }

  @PostMapping
  public ResponseEntity<ResumeDtos.ResumeSummaryResponse> create(
      @Valid @RequestBody ResumeDtos.CreateResumeRequest request
  ) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.createResume(userId, request));
  }

  @GetMapping
  public ResponseEntity<Page<ResumeDtos.ResumeSummaryResponse>> list(
      @PageableDefault(size = 10) Pageable pageable
  ) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.listResumes(userId, pageable));
  }

  @GetMapping("/{resumeId}")
  public ResponseEntity<ResumeDtos.ResumeDetailsResponse> details(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.getResumeDetails(userId, resumeId));
  }

  @PutMapping("/{resumeId}")
  public ResponseEntity<ResumeDtos.ResumeDetailsResponse> update(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.ResumeBulkUpdateRequest request
  ) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.updateResume(userId, resumeId, request));
  }

  @DeleteMapping("/{resumeId}")
  public ResponseEntity<Void> delete(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.deleteResume(userId, resumeId);
    return ResponseEntity.noContent().build();
  }

  // Education CRUD (bulk replace)
  @GetMapping("/{resumeId}/education")
  public ResponseEntity<java.util.List<ResumeDtos.EducationDto>> listEducation(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.getEducation(userId, resumeId));
  }

  @PutMapping("/{resumeId}/education")
  public ResponseEntity<Void> putEducation(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.EducationListRequest request
  ) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.replaceEducation(userId, resumeId, request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{resumeId}/education")
  public ResponseEntity<ResumeDtos.EducationDto> postEducation(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.EducationItemRequest item
  ) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.addEducation(userId, resumeId, item));
  }

  @DeleteMapping("/{resumeId}/education")
  public ResponseEntity<Void> deleteEducation(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.deleteEducation(userId, resumeId);
    return ResponseEntity.noContent().build();
  }

  // Experience CRUD (bulk replace)
  @GetMapping("/{resumeId}/experience")
  public ResponseEntity<java.util.List<ResumeDtos.ExperienceDto>> listExperience(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.getExperience(userId, resumeId));
  }

  @PutMapping("/{resumeId}/experience")
  public ResponseEntity<Void> putExperience(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.ExperienceListRequest request
  ) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.replaceExperience(userId, resumeId, request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{resumeId}/experience")
  public ResponseEntity<ResumeDtos.ExperienceDto> postExperience(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.ExperienceItemRequest item
  ) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.addExperience(userId, resumeId, item));
  }

  @DeleteMapping("/{resumeId}/experience")
  public ResponseEntity<Void> deleteExperience(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.deleteExperience(userId, resumeId);
    return ResponseEntity.noContent().build();
  }

  // Skills CRUD (bulk replace)
  @GetMapping("/{resumeId}/skills")
  public ResponseEntity<java.util.List<ResumeDtos.SkillDto>> listSkills(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.getSkills(userId, resumeId));
  }

  @PutMapping("/{resumeId}/skills")
  public ResponseEntity<Void> putSkills(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.SkillListRequest request
  ) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.replaceSkills(userId, resumeId, request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{resumeId}/skills")
  public ResponseEntity<ResumeDtos.SkillDto> postSkills(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.SkillItemRequest item
  ) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.addSkill(userId, resumeId, item));
  }

  @DeleteMapping("/{resumeId}/skills")
  public ResponseEntity<Void> deleteSkills(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.deleteSkills(userId, resumeId);
    return ResponseEntity.noContent().build();
  }

  // Projects CRUD (bulk replace)
  @GetMapping("/{resumeId}/projects")
  public ResponseEntity<java.util.List<ResumeDtos.ProjectDto>> listProjects(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.getProjects(userId, resumeId));
  }

  @PutMapping("/{resumeId}/projects")
  public ResponseEntity<Void> putProjects(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.ProjectListRequest request
  ) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.replaceProjects(userId, resumeId, request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{resumeId}/projects")
  public ResponseEntity<ResumeDtos.ProjectDto> postProjects(
      @PathVariable Long resumeId,
      @Valid @RequestBody ResumeDtos.ProjectItemRequest item
  ) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.addProject(userId, resumeId, item));
  }

  @DeleteMapping("/{resumeId}/projects")
  public ResponseEntity<Void> deleteProjects(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    resumeService.deleteProjects(userId, resumeId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{resumeId}/score")
  public ResponseEntity<ResumeDtos.ResumeScoreResponse> score(@PathVariable Long resumeId) {
    Long userId = securityContextUtil.currentUserId();
    return ResponseEntity.ok(resumeService.score(userId, resumeId));
  }

  @PostMapping("/{resumeId}/share")
  public ResponseEntity<ResumeDtos.ShareResponse> share(
      @PathVariable Long resumeId,
      HttpServletRequest servletRequest
  ) {
    Long userId = securityContextUtil.currentUserId();
    String token = resumeService.createShareToken(userId, resumeId);

    String shareUrl = ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/resumes/shared/{token}")
        .buildAndExpand(token)
        .toUriString();

    return ResponseEntity.ok(new ResumeDtos.ShareResponse(shareUrl));
  }

  @GetMapping("/shared/{shareToken}")
  public ResponseEntity<ResumeDtos.ResumeDetailsResponse> shared(@PathVariable String shareToken) {
    return ResponseEntity.ok(resumeService.getSharedResumeDetails(shareToken));
  }
}

