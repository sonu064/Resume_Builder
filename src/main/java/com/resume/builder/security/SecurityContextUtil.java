package com.resume.builder.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUtil {

  public Long currentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
      throw new IllegalStateException("No authenticated user");
    }
    return principal.getUserId();
  }

  public String currentEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
      throw new IllegalStateException("No authenticated user");
    }
    return principal.getUsername();
  }
}

