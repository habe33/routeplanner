package com.sixfold.routeplanner.controller;

import com.sixfold.routeplanner.AppStatus;
import com.sixfold.routeplanner.dto.AppStatusError;
import com.sixfold.routeplanner.exceptions.AppStatusException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AppStatusExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppStatusException.class)
    protected ResponseEntity<Object> handleAppStatus(AppStatusException ex) {
        AppStatusError error = new AppStatusError(HttpStatus.SERVICE_UNAVAILABLE,
                AppStatus.getStatus(),
                ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }
}
