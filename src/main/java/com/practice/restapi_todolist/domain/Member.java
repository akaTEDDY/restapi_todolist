package com.practice.restapi_todolist.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {
    private long id;
    private String nick;
    private String pw;
    private String email;

    public Member() {
        id = 0L;
        nick = "";
        pw = "";
        email = "";
    }
}
