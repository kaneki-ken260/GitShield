package com.backend.Aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleUnauthorizedException(HttpServletRequest request, AuthenticationException ex) {
        return new ResponseEntity<>("Authentication error: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}

