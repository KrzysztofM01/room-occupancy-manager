package com.sanesoft.roomoccupancymanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class OccupancyApiExceptionHandler {

    @ResponseBody
    @ExceptionHandler(InvalidRequestException.class)
    ResponseEntity<String> invalidRequestHandler(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
