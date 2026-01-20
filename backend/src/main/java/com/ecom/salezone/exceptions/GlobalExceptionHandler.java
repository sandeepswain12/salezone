package com.ecom.salezone.exceptions;

import com.ecom.salezone.dtos.ApiResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponseMessage apiResponseMessage = new ApiResponseMessage();
        apiResponseMessage.setMessage(ex.getMessage());
        apiResponseMessage.setSuccess(true);
        apiResponseMessage.setStatus(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponseMessage, HttpStatus.NOT_FOUND);
    }
}
