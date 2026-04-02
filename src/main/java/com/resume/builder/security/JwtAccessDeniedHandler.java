package com.resume.builder.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.builder.api.exception.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException
  ) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    ApiErrorResponse payload = new ApiErrorResponse(
        "FORBIDDEN",
        accessDeniedException.getMessage() == null ? "Access denied" : accessDeniedException.getMessage(),
        Map.of("path", request.getRequestURI())
    );

    response.getWriter().write(objectMapper.writeValueAsString(payload));
  }
}

