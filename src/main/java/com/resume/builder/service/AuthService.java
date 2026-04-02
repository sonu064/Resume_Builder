package com.resume.builder.service;

import com.resume.builder.api.dto.AuthDtos;
import com.resume.builder.api.exception.BadRequestException;
import com.resume.builder.api.exception.UnauthorizedException;
import com.resume.builder.domain.User;
import com.resume.builder.repository.UserRepository;
import com.resume.builder.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new BadRequestException("Email is already registered");
    }

    User user = new User();
    user.setEmail(request.email().toLowerCase().trim());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setFullName(request.fullName());

    User saved = userRepository.save(user);
    String token = jwtService.createToken(saved.getId(), saved.getEmail(), saved.getRole());
    return new AuthDtos.AuthResponse(token, saved.getId(), saved.getEmail());
  }

  @Transactional(readOnly = true)
  public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
    User user = userRepository.findByEmail(request.email().toLowerCase().trim())
        .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

    boolean matches = passwordEncoder.matches(request.password(), user.getPasswordHash());
    if (!matches) {
      throw new UnauthorizedException("Invalid email or password");
    }

    String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
    return new AuthDtos.AuthResponse(token, user.getId(), user.getEmail());
  }
}

