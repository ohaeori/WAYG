package com.example.chatapp;

import java.util.ArrayList;
/*realtime database Message structure*/
public class _Message {

    private String userName;
    private String message;

    public _Message() {
    }

    public _Message(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
