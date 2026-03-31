package com.resume.builder.service;

import com.resume.builder.api.dto.ResumeDtos;
import com.resume.builder.api.exception.NotFoundException;
import com.resume.builder.api.exception.UnauthorizedException;
import com.resume.builder.domain.*;
import com.resume.builder.repository.ResumeRepository;
import com.resume.builder.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;

  public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository) {
    this.resumeRepository = resumeRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public ResumeDtos.ResumeSummaryResponse createResume(Long userId, ResumeDtos.CreateResumeRequest request) {
    // Create a new resume owned by the user; sections will be filled later via PUT endpoints.
    Resume resume = new Resume();
    resume.setTemplateKey(defaultIfBlank(request.templateKey(), "classic"));
    resume.setFullName(request.fullName());
    resume.setHeadline(request.headline());
    resume.setEmail(request.email());
    resume.setPhone(request.phone());
    resume.setWebsite(request.website());
    resume.setLocation(request.location());
    resume.setSummary(request.summary());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found"));
    resume.setUser(user);

    Resume saved = resumeRepository.save(resume);
    return mapSummary(saved);
  }

  @Transactional(readOnly = true)
  public Page<ResumeDtos.ResumeSummaryResponse> listResumes(Long userId, Pageable pageable) {
    return resumeRepository.findByUserId(userId, pageable)
        .map(this::mapSummary);
  }

  @Transactional(readOnly = true)
  public ResumeDtos.ResumeDetailsResponse getResumeDetails(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);
    return mapDetails(resume);
  }

  @Transactional
  public ResumeDtos.ResumeDetailsResponse updateResume(Long userId, Long resumeId, ResumeDtos.ResumeBulkUpdateRequest request) {
    Resume resume = requireOwnedResume(userId, resumeId);

    resume.setTemplateKey(defaultIfBlank(request.templateKey(), resume.getTemplateKey()));
    resume.setFullName(request.fullName());
    resume.setHeadline(request.headline());
    resume.setEmail(request.email());
    resume.setPhone(request.phone());
    resume.setWebsite(request.website());
    resume.setLocation(request.location());
    resume.setSummary(request.summary());

    replaceEducation(resume, request.education());
    replaceExperience(resume, request.experience());
    replaceSkills(resume, request.skills());
    replaceProjects(resume, request.projects());

    Resume saved = resumeRepository.save(resume);
    return mapDetails(saved);
  }

  @Transactional
  public void deleteResume(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);
    resumeRepository.delete(resume);
  }

  @Transactional(readOnly = true)
  public List<ResumeDtos.EducationDto> getEducation(Long userId, Long resumeId) {
    return requireOwnedResume(userId, resumeId).getEducation().stream().map(this::mapEducation).toList();
  }

  @Transactional
  public void replaceEducation(Long userId, Long resumeId, ResumeDtos.EducationListRequest request) {
    Resume resume = requireOwnedResume(userId, resumeId);
    replaceEducation(resume, request.items());
  }

  @Transactional
  public ResumeDtos.EducationDto addEducation(Long userId, Long resumeId, ResumeDtos.EducationItemRequest item) {
    Resume resume = requireOwnedResume(userId, resumeId);
    int pos = item.position() == null ? resume.getEducation().size() : item.position();

    Education e = new Education();
    e.setResume(resume);
    e.setPosition(pos);
    e.setDegree(item.degree());
    e.setSchool(item.school());
    e.setLocation(item.location());
    e.setStartDate(item.startDate());
    e.setEndDate(item.endDate());
    e.setDescription(item.description());
    resume.getEducation().add(e);

    resumeRepository.save(resume);
    return mapEducation(e);
  }

  @Transactional
  public void deleteEducation(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);
    resume.getEducation().clear();
  }

  @Transactional(readOnly = true)
  public List<ResumeDtos.ExperienceDto> getExperience(Long userId, Long resumeId) {
    return requireOwnedResume(userId, resumeId).getExperience().stream().map(this::mapExperience).toList();
  }

  @Transactional
  public void replaceExperience(Long userId, Long resumeId, ResumeDtos.ExperienceListRequest request) {
    Resume resume = requireOwnedResume(userId, resumeId);
    replaceExperience(resume, request.items());
  }

  @Transactional
  public ResumeDtos.ExperienceDto addExperience(Long userId, Long resumeId, ResumeDtos.ExperienceItemRequest item) {
    Resume resume = requireOwnedResume(userId, resumeId);
    int pos = item.position() == null ? resume.getExperience().size() : item.position();

    Experience ex = new Experience();
    ex.setResume(resume);
    ex.setPosition(pos);
    ex.setRole(item.role());
    ex.setCompany(item.company());
    ex.setDescription(item.description());
    ex.setStartDate(item.startDate());
    ex.setEndDate(item.endDate());
    resume.getExperience().add(ex);

    resumeRepository.save(resume);
    return mapExperience(ex);
  }

  @Transactional
  public void deleteExperience(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);
    resume.getExperience().clear();
  }

  @Transactional(readOnly = true)
  public List<ResumeDtos.SkillDto> getSkills(Long userId, Long resumeId) {
    return requireOwnedResume(userId, resumeId).getSkills().stream().map(this::mapSkill).toList();
  }

  @Transactional
  public void replaceSkills(Long userId, Long resumeId, ResumeDtos.SkillListRequest request) {
    Resume resume = requireOwnedResume(userId, resumeId);
    replaceSkills(resume, request.items());
  }

  @Transactional
  public ResumeDtos.SkillDto addSkill(Long userId, Long resumeId, ResumeDtos.SkillItemRequest item) {
    Resume resume = requireOwnedResume(userId, resumeId);
    int pos = item.position() == null ? resume.getSkills().size() : item.position();

    Skill s = new Skill();
    s.setResume(resume);
    s.setPosition(pos);
    s.setName(item.name());
    s.setLevel(item.level());
    resume.getSkills().add(s);

    resumeRepository.save(resume);
    return mapSkill(s);
  }

  @Transactional
  public void deleteSkills(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);
    resume.getSkills().clear();
  }

  @Transactional(readOnly = true)
  public List<ResumeDtos.ProjectDto> getProjects(Long userId, Long resumeId) {
    return requireOwnedResume(userId, resumeId).getProjects().stream().map(this::mapProject).toList();
  }

  @Transactional
  public void replaceProjects(Long userId, Long resumeId, ResumeDtos.ProjectListRequest request) {
    Resume resume = requireOwnedResume(userId, resumeId);
    replaceProjects(resume, request.items());
  }

  @Transactional
  public ResumeDtos.ProjectDto addProject(Long userId, Long resumeId, ResumeDtos.ProjectItemRequest item) {
    Resume resume = requireOwnedResume(userId, resumeId);
    int pos = item.position() == null ? resume.getProjects().size() : item.position();

    Project p = new Project();
    p.setResume(resume);
    p.setPosition(pos);
    p.setName(item.name());
    p.setUrl(item.url());
    p.setDescription(item.description());
    p.setTechStack(item.techStack());
    resume.getProjects().add(p);

    resumeRepository.save(resume);
    return mapProject(p);
  }

  @Transactional
  public void deleteProjects(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);
    resume.getProjects().clear();
  }

  @Transactional(readOnly = true)
  public ResumeDtos.ResumeScoreResponse score(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);

    int score = 0;
    List<String> suggestions = new ArrayList<>();

    if (!isBlank(resume.getHeadline())) score += 10;
    if (!isBlank(resume.getSummary()) && resume.getSummary().length() >= 160) score += 10;

    boolean hasEmail = !isBlank(resume.getEmail());
    boolean hasPhone = !isBlank(resume.getPhone());
    if (hasEmail) score += 5;
    if (hasPhone) score += 5;
    if (!hasEmail) suggestions.add("Add an email contact");
    if (!hasPhone) suggestions.add("Add a phone contact");

    if (!resume.getEducation().isEmpty()) score += 15; else suggestions.add("Add Education entries");
    if (!resume.getExperience().isEmpty()) score += 25; else suggestions.add("Add Experience entries");
    if (!resume.getProjects().isEmpty()) score += 20; else suggestions.add("Add Projects to show impact");

    int skillCount = resume.getSkills().size();
    score += Math.min(20, skillCount * 2);
    if (skillCount < 8) suggestions.add("Add more Skills (aim for 8+).");

    score = Math.max(0, Math.min(100, score));

    // Cap suggestions length to keep UX clean.
    suggestions = suggestions.stream().filter(Objects::nonNull).limit(6).collect(Collectors.toList());

    return new ResumeDtos.ResumeScoreResponse(score, suggestions);
  }

  @Transactional
  public String createShareToken(Long userId, Long resumeId) {
    Resume resume = requireOwnedResume(userId, resumeId);
    String token = UUID.randomUUID().toString().replace("-", "");
    resume.setShareToken(token);
    resume.setShareTokenCreatedAt(Instant.now());
    resumeRepository.save(resume);
    return token;
  }

  @Transactional(readOnly = true)
  public ResumeDtos.ResumeDetailsResponse getSharedResumeDetails(String shareToken) {
    Resume resume = resumeRepository.findByShareToken(shareToken)
        .orElseThrow(() -> new NotFoundException("Resume not found for share token"));
    return mapDetails(resume);
  }

  private Resume requireOwnedResume(Long userId, Long resumeId) {
    Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new NotFoundException("Resume not found"));
    Long ownerId = resume.getUser() == null ? null : resume.getUser().getId();
    if (ownerId == null || !ownerId.equals(userId)) {
      throw new UnauthorizedException("You do not have access to this resume");
    }
    return resume;
  }

  private void replaceEducation(Resume resume, List<ResumeDtos.EducationItemRequest> items) {
    resume.getEducation().clear();
    if (items == null) return;
    for (int i = 0; i < items.size(); i++) {
      ResumeDtos.EducationItemRequest item = items.get(i);
      Education e = new Education();
      e.setResume(resume);
      e.setPosition(item.position() == null ? i : item.position());
      e.setDegree(item.degree());
      e.setSchool(item.school());
      e.setLocation(item.location());
      e.setStartDate(item.startDate());
      e.setEndDate(item.endDate());
      e.setDescription(item.description());
      resume.getEducation().add(e);
    }
  }

  private void replaceExperience(Resume resume, List<ResumeDtos.ExperienceItemRequest> items) {
    resume.getExperience().clear();
    if (items == null) return;
    for (int i = 0; i < items.size(); i++) {
      ResumeDtos.ExperienceItemRequest item = items.get(i);
      Experience ex = new Experience();
      ex.setResume(resume);
      ex.setPosition(item.position() == null ? i : item.position());
      ex.setRole(item.role());
      ex.setCompany(item.company());
      ex.setDescription(item.description());
      ex.setStartDate(item.startDate());
      ex.setEndDate(item.endDate());
      resume.getExperience().add(ex);
    }
  }

  private void replaceSkills(Resume resume, List<ResumeDtos.SkillItemRequest> items) {
    resume.getSkills().clear();
    if (items == null) return;
    for (int i = 0; i < items.size(); i++) {
      ResumeDtos.SkillItemRequest item = items.get(i);
      Skill s = new Skill();
      s.setResume(resume);
      s.setPosition(item.position() == null ? i : item.position());
      s.setName(item.name());
      s.setLevel(item.level());
      resume.getSkills().add(s);
    }
  }

  private void replaceProjects(Resume resume, List<ResumeDtos.ProjectItemRequest> items) {
    resume.getProjects().clear();
    if (items == null) return;
    for (int i = 0; i < items.size(); i++) {
      ResumeDtos.ProjectItemRequest item = items.get(i);
      Project p = new Project();
      p.setResume(resume);
      p.setPosition(item.position() == null ? i : item.position());
      p.setName(item.name());
      p.setUrl(item.url());
      p.setDescription(item.description());
      p.setTechStack(item.techStack());
      resume.getProjects().add(p);
    }
  }

  private ResumeDtos.ResumeSummaryResponse mapSummary(Resume resume) {
    return new ResumeDtos.ResumeSummaryResponse(
        resume.getId(),
        resume.getFullName(),
        resume.getTemplateKey(),
        resume.getUpdatedAt()
    );
  }

  private ResumeDtos.ResumeDetailsResponse mapDetails(Resume resume) {
    String resolvedProfileImage = resume.getProfileImageUrl();
    if (isBlank(resolvedProfileImage) && resume.getUser() != null) {
      resolvedProfileImage = resume.getUser().getProfileImageUrl();
    }

    return new ResumeDtos.ResumeDetailsResponse(
        resume.getId(),
        resume.getTemplateKey(),
        resume.getFullName(),
        resume.getHeadline(),
        resume.getEmail(),
        resume.getPhone(),
        resume.getWebsite(),
        resume.getLocation(),
        resume.getSummary(),
        resolvedProfileImage,
        resume.getShareToken(),
        resume.getEducation().stream().map(this::mapEducation).toList(),
        resume.getExperience().stream().map(this::mapExperience).toList(),
        resume.getSkills().stream().map(this::mapSkill).toList(),
        resume.getProjects().stream().map(this::mapProject).toList(),
        resume.getCreatedAt(),
        resume.getUpdatedAt()
    );
  }

  private ResumeDtos.EducationDto mapEducation(Education e) {
    return new ResumeDtos.EducationDto(
        e.getId(),
        e.getPosition(),
        e.getDegree(),
        e.getSchool(),
        e.getLocation(),
        e.getStartDate(),
        e.getEndDate(),
        e.getDescription()
    );
  }

  private ResumeDtos.ExperienceDto mapExperience(Experience e) {
    return new ResumeDtos.ExperienceDto(
        e.getId(),
        e.getPosition(),
        e.getRole(),
        e.getCompany(),
        e.getDescription(),
        e.getStartDate(),
        e.getEndDate()
    );
  }

  private ResumeDtos.SkillDto mapSkill(Skill s) {
    return new ResumeDtos.SkillDto(
        s.getId(),
        s.getPosition(),
        s.getName(),
        s.getLevel()
    );
  }

  private ResumeDtos.ProjectDto mapProject(Project p) {
    return new ResumeDtos.ProjectDto(
        p.getId(),
        p.getPosition(),
        p.getName(),
        p.getUrl(),
        p.getDescription(),
        p.getTechStack()
    );
  }

  private static String defaultIfBlank(String value, String defaultValue) {
    return isBlank(value) ? defaultValue : value;
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}

