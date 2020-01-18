package com.example.waygdemo;

/*realtime database Message structure*/
public class _Message {

    private String userName;
    private String message;
    private String lefttext;
    private String righttext;

    public _Message() { }
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

    public String getLefttext(){
        return lefttext;
    }

    public void setLefttext(){
        lefttext = userName+" "+message;
    }

    public String getRighttext(){
        return righttext;
    }

    public void setRighttext(){
        righttext = message;
    }
}
