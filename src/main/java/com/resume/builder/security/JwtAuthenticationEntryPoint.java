package com.resume.builder.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.builder.api.exception.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    ApiErrorResponse payload = new ApiErrorResponse(
        "UNAUTHORIZED",
        authException.getMessage() == null ? "Authentication required" : authException.getMessage(),
        Map.of(
            "path", request.getRequestURI()
        )
    );

    response.getWriter().write(objectMapper.writeValueAsString(payload));
  }
}

