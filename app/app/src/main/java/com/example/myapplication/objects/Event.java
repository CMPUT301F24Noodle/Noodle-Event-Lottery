package com.example.myapplication.objects;

import android.media.Image;

import java.util.ArrayList;
import java.util.Date;

/**
 * Event Class
 * Author: Erin-Marie
 * Purpose: Event object contains all information and methods for an event an organizer creates
 */
public class Event {
    //Other class objects connected to the event
    Facility facility; //Facility where the event is held
    UserProfile organizer; //User who organized the event

    //For the details of the event
    String eventName;
    Image eventPoster; //the event poster image
    Date eventDate; //date the actual event will occur
    Integer maxAttendents; //-1 if organizer does not want to restrict capacity

    //For status of the lottery
    Date lotteryOpens; //date entrants can start signing up for the event
    Date lotteryCloses; //date winners will be selected and notified
    Boolean eventOver; //False until the event attendance list is finalized, or the eventDate has passed
    ArrayList<UserProfile> entrantsList; //list of all entrants
    ArrayList<UserProfile> winnersList; //list of all users who won the lottery, may have max length equal to maxAttendents, unless maxAttendents == -1
    ArrayList<UserProfile> losersList; //list of all users who lost the lottery
}
