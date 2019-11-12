package com.sixfold.routeplanner.controller;

import com.sixfold.routeplanner.AppStatus;
import com.sixfold.routeplanner.dto.AppError;
import com.sixfold.routeplanner.exceptions.AppStatusException;
import com.sixfold.routeplanner.exceptions.ResultNotFoundException;
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
        AppError error = new AppError(HttpStatus.SERVICE_UNAVAILABLE,
                AppStatus.getStatus(),
                ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ResultNotFoundException.class)
    protected ResponseEntity<Object> handleResultNotFound(ResultNotFoundException ex) {
        AppError error = new AppError(HttpStatus.BAD_REQUEST,
                AppStatus.getStatus(),
                ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleValidationError(ConstraintViolationException ex) {
        AppError error = new AppError(HttpStatus.BAD_REQUEST,
                AppStatus.getStatus(),
                ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }
}
