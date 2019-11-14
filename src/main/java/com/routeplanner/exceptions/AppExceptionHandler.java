package com.routeplanner.exceptions;

import com.routeplanner.AppStatus;
import com.routeplanner.dto.AppError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppStatusException.class)
    protected ResponseEntity<Object> handleAppStatus(AppStatusException ex) {
        AppError error = new AppError(AppStatus.getStatus(),
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ExceptionHandler(ResultNotFoundException.class)
    protected ResponseEntity<Object> handleResultNotFound(ResultNotFoundException ex) {
        AppError error = new AppError(AppStatus.getStatus(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleValidationError(ConstraintViolationException ex) {
        AppError error = new AppError(AppStatus.getStatus(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }
}
