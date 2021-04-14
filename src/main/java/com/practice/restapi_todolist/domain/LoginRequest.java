package com.practice.restapi_todolist.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String nick;
    private String pw;
}
