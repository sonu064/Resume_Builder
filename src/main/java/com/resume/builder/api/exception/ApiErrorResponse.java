package com.resume.builder.api.exception;

import java.util.Map;

public record ApiErrorResponse(
    String code,
    String message,
    Map<String, Object> details
) {}

