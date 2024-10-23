package com.example.myapplication.objects.eventClasses;

import android.media.Image;

import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

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
    Boolean geoLocation; //False if organizer does not require entrants to have geoLocation on
    //TODO: need QR code attribute idk how that is stored though

    //For status of the lottery
    Date lotteryCloses; //date winners will be selected and notified
    Boolean eventOver; //False until the event attendance list is finalized, or the eventDate has passed
    Boolean eventFull; //False if there is still room for entrants, or if maxAttendents == -1
    ArrayList<UserProfile> entrantsList; //list of all entrants
    ArrayList<UserProfile> winnersList; //list of all users who won the lottery, may have max length equal to maxAttendents, unless maxAttendents == -1
    ArrayList<UserProfile> losersList; //list of all users who lost the lottery

    //Event Class Constructor
    public Event(Facility facility, UserProfile organizer, String eventName, Image eventPoster, Date eventDate, Integer maxEntrants, Date lotteryCloses, Boolean geoLocation) {
        this.facility = facility;
        this.organizer = organizer;
        this.eventName = eventName;
        this.eventPoster = eventPoster; //can be null
        this.eventDate = eventDate; //event date is converted to a date when the input is taken
        this.maxEntrants = maxEntrants;
        this.lotteryCloses = lotteryCloses;
        this.geoLocation = geoLocation;
        this.eventFull = Boolean.FALSE; // event capacity cannot be 0, so it is always false at init
        this.entrantsList = new ArrayList<UserProfile>(); //have to intialize so .size() wont return null
        this.winnersList = new ArrayList<UserProfile>(); //have to intialize so .size() wont return null
        this.losersList = new ArrayList<UserProfile>(); //have to intialize so .size() wont return null

        //TODO: Need to create QR code and do something with hash data

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
        setEventFull(); //if they choose to add a capacity, update whether the event is already full. No entrants are removed if the event is already over the capacity.
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

    public ArrayList<UserProfile> getEntrants(){
        return this.entrantsList;
    }

    /**
     * returns count of how many users have entered the event lottery
     * @return
     */
    public Integer countEntrants(){
        return this.entrantsList.size();
    }

    /**
     * Author: Erin-Marie
     * Called anytime an entrant is added or removed from the entrants list
     * Updates whether the event entrant limit has been reached
     * TESTME: if capacity is already maxed,
     *         if capacity is 1, then add new entrant, now check that it is returning maxed
     */
    public void setEventFull(){
        if (this.maxEntrants == -1 | this.maxEntrants > this.entrantsList.size()){
            this.eventFull = Boolean.FALSE;
        } else {
            this.eventFull = Boolean.TRUE;
        }
    }

    public Boolean getEventFull(){
        return this.eventFull;
    }

    /**
     * Author: Erin-Marie
     * Adds an entrant to the list of entrants for the event
     * Checks that the entrant is not already in the list of entrants
     * @param entrant UserProfile that wants to enter event lottery
     * @return 1 if the user was added to the entrant list, or 0 if not
     * calling function needs to add the event to the users myevents list, dependent on the return value of addEntrant
     * TODO: needs to update firebase db
     * TESTME: if capacity it maxed, should return 0
     *         if capacity is not maxed, should return 1 and check that the entrant is now in the entrantsList
     */
    public int addEntrant(UserProfile entrant){
        //check that entrant is not already in the entrantList, and the event is not full
        if (!this.entrantsList.contains(entrant) && this.eventFull == Boolean.FALSE){
            this.entrantsList.add(entrant);
            //entrant.addEvent(this); //add the event to the entrants list of events
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
     * TODO: needs to update firebase db
     * TESTME: check that entrant is actually removed from the entrantList
     */
    public void removeEntrant(UserProfile entrant){
        this.entrantsList.remove(entrant);
        entrant.leaveEvent(this); // remove the event from the entrants list of events
        setEventFull(); //update whether the event is full
    }

}
