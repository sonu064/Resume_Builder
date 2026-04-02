package com.resume.builder.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    Map<String, Object> details = new HashMap<>();
    details.put("path", request.getRequestURI());
    details.put("fieldErrors", ex.getBindingResult().getFieldErrors().stream().map(fe -> {
      Map<String, String> e = new HashMap<>();
      e.put("field", fe.getField());
      e.put("message", fe.getDefaultMessage());
      return e;
    }).toList());

    ApiErrorResponse payload = new ApiErrorResponse(
        "VALIDATION_ERROR",
        "Request validation failed",
        details
    );
    return ResponseEntity.badRequest().body(payload);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleNotReadable(
      HttpMessageNotReadableException ex,
      HttpServletRequest request
  ) {
    ApiErrorResponse payload = new ApiErrorResponse(
        "BAD_REQUEST",
        "Malformed request body",
        Map.of("path", request.getRequestURI(), "reason", ex.getMostSpecificCause().getMessage())
    );
    return ResponseEntity.badRequest().body(payload);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(
      NotFoundException ex,
      HttpServletRequest request
  ) {
    ApiErrorResponse payload = new ApiErrorResponse(
        "NOT_FOUND",
        ex.getMessage(),
        Map.of("path", request.getRequestURI())
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiErrorResponse> handleUnauthorized(
      UnauthorizedException ex,
      HttpServletRequest request
  ) {
    ApiErrorResponse payload = new ApiErrorResponse(
        "UNAUTHORIZED",
        ex.getMessage(),
        Map.of("path", request.getRequestURI())
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payload);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiErrorResponse> handleBadRequest(
      BadRequestException ex,
      HttpServletRequest request
  ) {
    ApiErrorResponse payload = new ApiErrorResponse(
        "BAD_REQUEST",
        ex.getMessage(),
        Map.of("path", request.getRequestURI())
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGeneric(
      Exception ex,
      HttpServletRequest request
  ) {
    ApiErrorResponse payload = new ApiErrorResponse(
        "INTERNAL_ERROR",
        "Unexpected server error",
        Map.of("path", request.getRequestURI())
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
  }
}

