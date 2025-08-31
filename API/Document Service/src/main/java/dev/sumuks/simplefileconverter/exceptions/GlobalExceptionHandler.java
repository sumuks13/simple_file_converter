package dev.sumuks.simplefileconverter.exceptions;

import dev.sumuks.simplefileconverter.beans.ApiResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseBean<String>> handleExceptions(Exception e){
        log.error("Error: ",e);
        ApiResponseBean<String> responseBody = ApiResponseBean.failure("Something went wrong. Please try again later.");
        return ResponseEntity.internalServerError().body(responseBody);
    }
}
