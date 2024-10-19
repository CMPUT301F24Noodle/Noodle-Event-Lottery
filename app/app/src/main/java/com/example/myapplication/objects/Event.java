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
    Date lotteryCloses; //date winners will be selected and notified
    Boolean eventOver; //False until the event attendance list is finalized, or the eventDate has passed
    ArrayList<UserProfile> entrantsList; //list of all entrants
    ArrayList<UserProfile> winnersList; //list of all users who won the lottery, may have max length equal to maxAttendents, unless maxAttendents == -1
    ArrayList<UserProfile> losersList; //list of all users who lost the lottery

    //Event Class Constructor
    public Event(Facility facility, UserProfile organizer, String eventName, Image eventPoster, Date eventDate, Integer maxAttendents, Date lotteryCloses) {
        this.facility = facility;
        this.organizer = organizer;
        this.eventName = eventName;
        this.eventPoster = eventPoster;
        this.eventDate = eventDate;
        this.maxAttendents = maxAttendents;
        this.lotteryCloses = lotteryCloses;

    }

    //Getters and Setters
    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public UserProfile getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UserProfile organizer) {
        this.organizer = organizer;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Image getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(Image eventPoster) {
        this.eventPoster = eventPoster;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getMaxAttendents() {
        return maxAttendents;
    }

    public void setMaxAttendents(Integer maxAttendents) {
        this.maxAttendents = maxAttendents;
    }

    public Date getLotteryCloses() {
        return lotteryCloses;
    }

    public void setLotteryCloses(Date lotteryCloses) {
        this.lotteryCloses = lotteryCloses;
    }

    public Boolean getEventOver() {
        return eventOver;
    }

    public void setEventOver(Boolean eventOver) {
        this.eventOver = eventOver;
    }

    /**
     * Author: Erin-Marie
     * Adds an entrant to the list of entrants for the event
     * Checks that the entrant is not already in the list of entrants
     * @param entrant UserProfile that wants to enter event lottery
     * @return 1 if the user was added to the entrant list, or 0 if not
     * Still needs: test, need to check if event has a registration limit? , needs to update firebase db
     */
    public int addEntrant(UserProfile entrant){
        //check that entrant is not already in the entrantList
        if (!this.entrantsList.contains(entrant)){
            this.entrantsList.add(entrant);
            return 1; }

        //return 0 if user is not added to the list
        return 0;
    }

    /**
     * Author: Erin-Marie
     * Adds an entrant to the list of entrants for the event
     * Checks that the entrant is not already in the list of entrants
     * @param entrant UserProfile that wants to un-enter the event lottery
     * Still needs: test, needs to update firebase db
     */
    public void removeEntrant(UserProfile entrant){
        this.entrantsList.remove(entrant);
    }

}
