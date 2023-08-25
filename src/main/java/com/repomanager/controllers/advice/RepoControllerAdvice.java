package com.repomanager.controllers.advice;

import com.repomanager.models.exceptions.DefaultExceptionResponse;
import com.repomanager.models.exceptions.GenericException;
import com.repomanager.models.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.repomanager.consts.ErrorResponseConsts.*;

@RestControllerAdvice
@Slf4j
public class RepoControllerAdvice {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<DefaultExceptionResponse> handleHttpMediaTypeNotSupportedException() {

        log.error("406: " + REQUEST_NOT_VALID);
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(DefaultExceptionResponse.builder()
                        .status(HttpStatus.NOT_ACCEPTABLE.value())
                        .message(REQUEST_NOT_VALID)
                        .build());

    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<DefaultExceptionResponse> handleUserNotFoundException() {

        log.error("404: " + USER_NOT_FOUND);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(DefaultExceptionResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(USER_NOT_FOUND)
                        .build());

    }

    @ExceptionHandler(GenericException.class)
    ResponseEntity<DefaultExceptionResponse> handleUserNotFoundException(Exception exception) {

        log.error("500: " + EXTERNAL_API_ERROR + "Message: " + exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(DefaultExceptionResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(EXTERNAL_API_ERROR)
                        .build());

    }

}
