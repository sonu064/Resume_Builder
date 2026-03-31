package com.resume.builder.api.controller;

import com.resume.builder.api.dto.AuthDtos;
import com.resume.builder.domain.User;
import com.resume.builder.repository.UserRepository;
import com.resume.builder.security.SecurityContextUtil;
import com.resume.builder.service.FileStorageService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final SecurityContextUtil securityContextUtil;
  private final UserRepository userRepository;
  private final FileStorageService fileStorageService;

  public UserController(SecurityContextUtil securityContextUtil, UserRepository userRepository, FileStorageService fileStorageService) {
    this.securityContextUtil = securityContextUtil;
    this.userRepository = userRepository;
    this.fileStorageService = fileStorageService;
  }

  @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AuthDtos.ProfileImageUploadResponse> uploadProfileImage(
      @RequestPart("image") @NotNull MultipartFile image
  ) {
    Long userId = securityContextUtil.currentUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new com.resume.builder.api.exception.NotFoundException("User not found"));

    String url = fileStorageService.storeProfileImage(user.getId(), image);
    user.setProfileImageUrl(url);
    userRepository.save(user);

    return ResponseEntity.ok(new AuthDtos.ProfileImageUploadResponse(url));
  }
}

