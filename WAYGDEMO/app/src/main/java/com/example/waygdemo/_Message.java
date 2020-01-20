package com.example.waygdemo;

/*realtime database Message structure*/
public class _Message {

    private String userName;
    private String message;
    private String Lefttext;
    private String Righttext;
    private String nametext;

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
        return Lefttext;
    }

    public void setLefttext() {
        this.Lefttext = message;
    }

    public String getRighttext(){
        return Righttext;
    }

    public void setRighttext() {
        this.Righttext = message;
    }

    public String getNametext(){
        return nametext;
    }

    public void setNametext(){
        this.nametext = userName;
    }
}
