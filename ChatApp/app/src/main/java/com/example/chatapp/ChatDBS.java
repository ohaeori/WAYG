package com.example.chatapp;

import java.util.ArrayList;

public class ChatDBS {

    private String userName;
    private String message;
    private ArrayList<String> participants = new ArrayList<>();

    public ChatDBS() {
    }

    public ChatDBS(String userName, String message) {
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

    public String getParticipants(){
        String all_participants = new String();
        for(String participant : participants)
            all_participants += participant + ", ";
        return all_participants.substring(0, all_participants.length()-2);
    }

    public void setParticipants(String participant){
        participants.add(participant);
    }

    public int getNum_of_user(){
        return participants.size();
    }

}
