package Meraki.Hub.Management.System.exceptions;

import jakarta.security.auth.message.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================================================
       PERMISSION EXCEPTIONS
       ========================================================= */
    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<?> handleNotFound(PermissionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error(ex.getMessage(), 404));
    }

    @ExceptionHandler(PermissionAlreadyExistsException.class)
    public ResponseEntity<?> handleDuplicate(PermissionAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error(ex.getMessage(), 409));
    }

    /* =========================================================
       AUTHENTICATION EXCEPTIONS
       ========================================================= */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error(ex.getMessage(), 401));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error(ex.getMessage(), 401));
    }

    /* =========================================================
       ADMIN / USER ACCESS EXCEPTIONS
       ========================================================= */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        // Handles admin access denied, account disabled, or other runtime errors
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error(ex.getMessage(), 400));
    }

    /* =========================================================
       GENERIC EXCEPTIONS
       ========================================================= */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("Internal server error: " + ex.getMessage(), 500));
    }

    /* =========================================================
       ERROR RESPONSE STRUCTURE
       ========================================================= */
    private Map<String, Object> error(String message, int status) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "error", message
        );
    }
}
