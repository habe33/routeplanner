package com.sixfold.routeplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppStatusError {

    private HttpStatus status;
    private String appStatus;
    private String message;

}
