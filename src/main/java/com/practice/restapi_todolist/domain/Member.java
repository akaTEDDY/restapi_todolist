package com.practice.restapi_todolist.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Member {
    private long id;
    private String nick;
    private String pw;
    private String email;
}
