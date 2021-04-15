package com.practice.restapi_todolist.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CustomNotFoundException extends CustomException {
    public CustomNotFoundException(HttpStatus HttpStatus, Map<String, String> param, String description) {
        super(HttpStatus, param, "존재하지 않는 자원입니다. : " + description);
    }
}
