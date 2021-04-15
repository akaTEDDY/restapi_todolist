package com.practice.restapi_todolist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class CustomException extends Throwable {
    public HttpStatus HttpStatus;
    public Map<String, String> param;
    public String description;
}
