package com.practice.restapi_todolist.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CustomBadRequestException extends CustomException {
    public CustomBadRequestException(HttpStatus HttpStatus, Map<String, String> param, String description) {
        super(HttpStatus, param, "잘못된 요청입니다. : " + description);
    }
}
