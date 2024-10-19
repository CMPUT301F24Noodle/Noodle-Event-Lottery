package com.example.myapplication.objects.eventClasses;

import android.media.Image;

import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserProfile;

import java.util.ArrayList;
import java.util.Date;

/**
 * Event Class
 * Author: Erin-Marie
 * Purpose: Event object contains all information and methods for an event an organizer creates
 * TODO: make method for creating a QR code
 *       make methods for ending the contest and selecting winners
 *       make getters and setters for the winnersList and loserList
 *       make method for notifying winners and losers and sending their invitations
 *       make method for making the list of confirmed attendants
 *       make method for selecting a new winner if someone rejects an invitation
 *       add db interaction through EventDB controller class for all methods
 *       make method for organizer to send a notification with custom message to all users of specific group
 */
public class Event {
    //Other class objects connected to the event
    Facility facility; //Facility where the event is held
    UserProfile organizer; //User who organized the event

    //For the details of the event
    String eventName;
    Image eventPoster; //the event poster image
    Date eventDate; //date the actual event will occur
    Integer maxEntrants; //-1 if organizer does not want to restrict capacity
    //TODO: need QR code attribute idk how that is stored though

    //For status of the lottery
    Date lotteryCloses; //date winners will be selected and notified
    Boolean eventOver; //False until the event attendance list is finalized, or the eventDate has passed
    Boolean eventFull; //False if there is still room for entrants, or if maxAttendents == -1
    ArrayList<UserProfile> entrantsList; //list of all entrants
    ArrayList<UserProfile> winnersList; //list of all users who won the lottery, may have max length equal to maxAttendents, unless maxAttendents == -1
    ArrayList<UserProfile> losersList; //list of all users who lost the lottery

    //Event Class Constructor
    public Event(Facility facility, UserProfile organizer, String eventName, Image eventPoster, Date eventDate, Integer maxEntrants, Date lotteryCloses) {
        this.facility = facility;
        this.organizer = organizer;
        this.eventName = eventName;
        this.eventPoster = eventPoster;
        this.eventDate = eventDate;
        this.maxEntrants = maxEntrants;
        this.lotteryCloses = lotteryCloses;
        this.eventFull = Boolean.FALSE;
        //TODO: Need to create QR code

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

    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
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
     * Called anytime an entrant is added or removed from the entrants list
     * Updates whether the event entrant limit has been reached
     * TODO: write tests for this method
     */
    public void setEventFull(){
        if (this.maxEntrants == -1 | this.maxEntrants > this.entrantsList.size()){
            this.eventFull = Boolean.FALSE;
        } else {
            this.eventFull = Boolean.TRUE;
        }
    }

    /**
     * Author: Erin-Marie
     * Adds an entrant to the list of entrants for the event
     * Checks that the entrant is not already in the list of entrants
     * @param entrant UserProfile that wants to enter event lottery
     * @return 1 if the user was added to the entrant list, or 0 if not
     * TODO: write tests
     *       needs to update firebase db
     */
    public int addEntrant(UserProfile entrant){
        //check that entrant is not already in the entrantList, and the event is not full
        if (!this.entrantsList.contains(entrant) && this.eventFull == Boolean.FALSE){
            this.entrantsList.add(entrant);
            setEventFull(); //update whether the event is full
            return 1; }

        //return 0 if user is not added to the list
        return 0;
    }

    /**
     * Author: Erin-Marie
     * Adds an entrant to the list of entrants for the event
     * Checks that the entrant is not already in the list of entrants
     * @param entrant UserProfile that wants to un-enter the event lottery
     * TODO: write tests
     *       needs to update firebase db
     */
    public void removeEntrant(UserProfile entrant){
        this.entrantsList.remove(entrant);
        setEventFull(); //update whether the event is full
    }

}
