package com.web_hub.web_hub.exception;


import com.web_hub.web_hub.errorHandler.ApiErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. CUSTOM APP EXCEPTIONS (e.g., "User already exists", "OTP expired")
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthException(AuthException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), null);
    }

    // 2. SPRING SECURITY: WRONG CREDENTIALS
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password", null);
    }

    // 3. SPRING SECURITY: UNAUTHENTICATED (Missing/Invalid JWT)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Full authentication is required to access this resource", null);
    }

    // 4. SPRING SECURITY: FORBIDDEN (Has JWT, but wrong Role)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to perform this action", null);
    }

    // 5. JAKARTA VALIDATION (@Valid failed on JSON body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid input data", errors);
    }

    // 6. DATABASE CONSTRAINTS (e.g., duplicate unique keys like email)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Passes through your clean service exceptions directly to the JSON response payload
        return buildResponse(HttpStatus.CONFLICT, "Database Conflict", ex.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), null);
    }

    // 7. GENERIC FALLBACK (Catches NullPointerExceptions, logic crashes, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex) {
        // In production, you'd log the 'ex.getMessage()' here but return a generic message to the user
        System.err.println("CRITICAL ERROR: " + ex.getMessage());
        ex.printStackTrace();

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", "An unexpected error occurred. Please try again later.", null);
    }

    /* =======================================================
       HELPER METHOD TO BUILD CONSISTENT JSON RESPONSES
       ======================================================= */
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String errorType,
            String message,
            Map<String, String> validationErrors
    ) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                status.value(),
                errorType,
                message,
                validationErrors,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
