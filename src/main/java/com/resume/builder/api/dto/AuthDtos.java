package com.resume.builder.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

  public record RegisterRequest(
      @NotBlank
      @Email
      String email,
      @NotBlank
      @Size(min = 8, max = 200)
      String password,
      String fullName
  ) {}

  public record LoginRequest(
      @NotBlank
      @Email
      String email,
      @NotBlank
      @Size(min = 8, max = 200)
      String password
  ) {}

  public record AuthResponse(
      String token,
      Long userId,
      String email
  ) {}

  public record ProfileImageUploadResponse(
      String profileImageUrl
  ) {}
}

