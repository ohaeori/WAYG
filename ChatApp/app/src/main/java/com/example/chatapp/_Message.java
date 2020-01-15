package com.example.chatapp;

/*realtime database Message structure*/
public class _Message {

    private String userName;
    private String message;
    private int res=R.layout.left_row;

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

    public int getRes(){ return res; }

    public void setRes(int res) { this.res = res; }
}
