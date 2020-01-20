package com.example.waygdemo;

import java.util.ArrayList;

/*realtime DataBase Chating room Structure*/
public class ChatDBS {
    private ArrayList<String> participants;
    private String arrival;
    private String departure;

    public ChatDBS(){}
    public ChatDBS(String username, String arrival, String departure){
        participants = new ArrayList<>();
        participants.add(username);
        this.arrival = arrival;
        this.departure = departure;
    }

    public void setArrival(String arrival){
        this.arrival = arrival;
    }

    public String getArrival(){
        return this.arrival;
    }

    public void setDeparture(String departure){
        this.departure = departure;
    }

    public String getDeparture(){
        return this.departure;
    }

    public ArrayList<String> getParticipants() { return participants; }

    public void addParticipants(String participant){
        participants.add(participant);
    }

    public int getNum_of_user(){
        return participants.size();
    }

    public String getParticipantsList(){
        String all_participants = new String();
        for(String participant : participants)
            all_participants += participant + "\n";
        return all_participants.substring(0, all_participants.length()-1);
    }
}
