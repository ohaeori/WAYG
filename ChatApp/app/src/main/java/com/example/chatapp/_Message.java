package com.example.chatapp;

/*realtime database Message structure*/
public class _Message {

    private String userName;
    private String message;
    private String rowtext1;
    private String rowtext2;

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

    public void setRowtext1(){
        rowtext1 = userName+" "+message;
    }
    public void setRowtext2(){
        rowtext2 = message;
    }

    public String getRowtext1(){
        return rowtext1;
    }

    public String getRowtext2(){
        return rowtext2;
    }

}
