package com.practice.restapi_todolist.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Follow {
    private long id;
    private String toNick;
    private String fromNick;
    private String status;

    public Follow() {
        id = 0L;
        toNick = "";
        fromNick = "";
        status = "";
    }
}
