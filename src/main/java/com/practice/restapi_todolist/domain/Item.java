package com.practice.restapi_todolist.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Item {
    private long id;
    private String title;
    private String note;
    private String status;
    private Date regDate;
    private Date dueDate;
    private String userNick;

    public Item() {
        id = 0L;
        title = "";
        note = "";
        status = "";
        regDate = null;
        regDate = dueDate;
        userNick = "";
    }
}
