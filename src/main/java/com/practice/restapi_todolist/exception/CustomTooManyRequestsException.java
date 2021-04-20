package com.practice.restapi_todolist.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CustomTooManyRequestsException extends CustomException {
    public CustomTooManyRequestsException(HttpStatus HttpStatus, Map<String, String> param, String description) {
        super(HttpStatus, param, "요청이 너무 많습니다. : " + description);
    }
}
