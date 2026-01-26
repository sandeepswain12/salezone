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

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        logger.error("RESOURCE NOT FOUND EXCEPTION OCCURRED : {}", ex.getMessage());

        ApiResponseMessage apiResponseMessage = new ApiResponseMessage();
        apiResponseMessage.setMessage(ex.getMessage());
        apiResponseMessage.setSuccess(true);
        apiResponseMessage.setStatus(HttpStatus.NOT_FOUND);

        logger.info("RETURNING RESPONSE FOR RESOURCE NOT FOUND : {}", apiResponseMessage);

        return new ResponseEntity<>(apiResponseMessage, HttpStatus.NOT_FOUND);
    }
}
