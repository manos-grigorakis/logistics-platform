package com.manosgrigorakis.logisticsplatform.common.exception;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponse;
import com.manosgrigorakis.logisticsplatform.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Field validation error - 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleFieldValidationException(MethodArgumentNotValidException exc) {
        Map<String, String> details = new HashMap<>();

        exc.getBindingResult().getFieldErrors().forEach(error ->
                details.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Field Validation Failed",
                details
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.BAD_REQUEST);
    }

    // Token expired error - 400
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpiredTokenException(TokenExpiredException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Token has expired",
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.BAD_REQUEST);
    }

    // Bad Request - 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exc.getMessage(),
                exc.getErrorCode(),
                exc.getDetails()
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.BAD_REQUEST);
    }

    // Bad credentials - 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid email or password",
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.UNAUTHORIZED);
    }

    // Account disabled - 401
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabledAccountException(DisabledException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Disabled account",
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.UNAUTHORIZED);
    }

    // Account locked - 401
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLockedAccountException(LockedException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Locked account",
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.UNAUTHORIZED);
    }

    // Forbidden - 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                exc.getMessage(),
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.FORBIDDEN);
    }

    // Not found - 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exc.getMessage(),
                exc.getErrorCode(),
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.NOT_FOUND);
    }

    // Duplicate Entry - 409
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEntryException(DuplicateEntryException exc) {
        Map<String, Object> details = Map.of(
                "duplicateField", exc.getField(),
                "duplicateValue", exc.getValue()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exc.getMessage(),
                exc.getErrorCode(),
                details
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.CONFLICT);
    }

    // Delete Conflict Due to Data Integrity Violation - 409
    @ExceptionHandler(DeleteConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleDeleteConflictException(DeleteConflictException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exc.getMessage(),
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.CONFLICT);
    }

    // Conflict - 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictException(ConflictException exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exc.getMessage(),
                exc.getErrorCode(),
                exc.getDetails()
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.CONFLICT);
    }

    // Server error - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exc) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exc.getMessage(),
                null
        );

        return new ResponseEntity<>(new ApiResponse<>(response), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
