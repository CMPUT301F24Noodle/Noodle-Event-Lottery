package com.example.myapplication.objects;

import java.util.Date;

public class Event {
    Facility facility; //Facility where the event is held
    UserProfile organizer; //User who organized the event
    String eventName;
    Date eventDate; //date the actual event will occur
    Date lotteryOpens; //date entrants can start signing up for the event
    Date lotteryCloses; //date winners will be selected and notified

}
