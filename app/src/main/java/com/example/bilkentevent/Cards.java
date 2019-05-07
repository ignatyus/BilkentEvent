package com.example.bilkentevent;

import java.io.Serializable;

public class Cards implements Serializable {

    private String clubID;
    private String eventID;

    public Cards(String clubID, String eventID){

        this.clubID = clubID;
        this.eventID = eventID;
    }


    public String getClubID() {
        return clubID;
    }

    public void setClubID(String userID) {
        this.clubID = userID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

}

