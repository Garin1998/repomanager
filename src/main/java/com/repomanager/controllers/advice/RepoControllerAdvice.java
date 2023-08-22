package com.repomanager.controllers.advice;

import com.repomanager.models.errors.DefaultErrorResponse;
import com.repomanager.models.errors.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RepoControllerAdvice {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<DefaultErrorResponse> handleHttpMediaTypeNotSupportedException() {

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(DefaultErrorResponse.builder()
                        .status(HttpStatus.NOT_ACCEPTABLE.value())
                        .message("Request content not valid")
                        .build());

    }

    @ExceptionHandler(UserNotFound.class)
    ResponseEntity<DefaultErrorResponse> handleUserNotFoundException() {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(DefaultErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message("User not found")
                        .build());

    }

}
