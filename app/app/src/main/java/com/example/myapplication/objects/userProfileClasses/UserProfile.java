package com.example.myapplication.objects.userProfileClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;

import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.facilityClasses.Facility;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Author: Erin-Marie
 * Class for a user object, stores any profile data they choose to add, and has method to get their UUID
 * TODO: connect UserProfile to db, to get the users db, and their collection
 */
public class UserProfile {

    String firstName;
    String lastName;
    String email;
    Image profilePicture;
    Integer privileges; //0 is default, means they are just an entrant, 1 means they also have organizer privilege
    ArrayList<Event> myEvents; //the users ENTERED events
    String uuid;
    //organizer privilege attributes
    ArrayList<Event> myOrgEvents; //the users ORGANIZED events
    Facility myFacility ; //the users facility they created, can only have one

    Boolean isAdmin; //true if the user has admin privileges

    //QUESTION: should these be within sharedPreferences, not stored within the UserProfile itself?
    Boolean allowNotifs = Boolean.TRUE; //False if they do not want to receive notifications, True if they do allow notifications
    Boolean geoLocationOn; //True if they allow geoLocation, False if not


    public UserProfile() {} //need for firebase

    /**
     * Author: Erin-Marie
     * UserProfile is initially created with ony default values, since the user is not required to enter their profile information
     * If the user wants to edit anything, it will be done through the getter and setter methods called from the ProfileActivity
     */
    public UserProfile(String uuid) {

        this.firstName = "None";
        this.lastName = "None";
        this.email = "None";
        this.privileges = 0; //defaults to entrant privileges
        this.allowNotifs = Boolean.TRUE; //defaults to allow notifications
        this.geoLocationOn = Boolean.FALSE; //defaults to false, need to ask user for permission first
        this.isAdmin = Boolean.FALSE;
        this.uuid = uuid;



        //TODO: make a res file with a default profile picture to use until a user submits their own
        //this.profilePicture =
    }

    public Facility getMyFacility() {
        return myFacility;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public String getUuid(){
        return uuid;
    }

    //QUESTION: do we put input validation here or within the EditUserProfile fragment that will call these setters?
    public String getFirstName() {
        return firstName;
    }

    //TODO: need to update firebase
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    //TODO: need to update firebase
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    //TODO: need to update firebase
    public void setEmail(String email) {
        this.email = email;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    //TODO: need to update firebase
    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getPrivileges() {
        return privileges;
    }

    //TODO: need to update firebase
    public void setPrivileges(Integer privileges) {
        this.privileges = privileges;
    }

    public ArrayList<Event> getMyEvents() {
        return myEvents;
    }

    public ArrayList<Event> getMyOrgEvents() {
        return myOrgEvents;
    }

    public Boolean getAllowNotifs() {
        return allowNotifs;
    }

    //TODO: need to update firebase
    public void setAllowNotifs(Boolean allowNotifs) {
        this.allowNotifs = allowNotifs;
    }

    //MAYBE: if geoLocation is stored within sharedPreferences this will need to be more complex
    public Boolean getGeoLocationOn() {
        return geoLocationOn;
    }

    //TODO: need to update firebase
    //MAYBE: if geoLocation is stored within sharedPreferences this will need to be more complex
    public void setGeoLocationOn(Boolean geoLocationOn) {
        this.geoLocationOn = geoLocationOn;
    }

    public Facility getFacility(){
        return this.myFacility;
    }

    /**
     * Author: Erin-Marie
     * connects a facility instance to the user who created it
     * grants the user organizer privileges
     * @param facility Facility the user owns
     * @return 0 if the facility is successfully attached to the user, or 1 if not (user already has a facility)
     * TESTME: test that the facility has been set
     *         test that if the user already has a facility, that the facility is not being overwritten
     */
    public Integer setFacility(Facility facility){
        //Check that the user does not already have a facility
        if (myFacility == null) {
            this.myFacility = facility;
            //creating a facility gives the user Organizer privileges, increase privileges
            this.privileges = 1;
            return 0; //return successful

        } else {
            return 1; //could not add facility, user already owns a facility
        }
    }

    /**
     * Author: Erin-Marie
     * checks that the facility is the users facility
     * removes the facility from the owners profile
     * demotes the users privileges to just entrant (0)
     * @param facility facility that is being deleted
     * TODO: update the users profile in firebase db
     * TESTME: test that the facility has been removed from the users profile
     *         test that the user now only has entrant privileges
     */
    public void removeFacility(Facility facility){
        if (myFacility == facility){
            this.myFacility = null;
            this.privileges = 0; //removing your facility means you are no longer an organizer
            //MAYBE: need to delete all events that they were hosting at this facility?
            //       deleting the facility from the db will be done by the calling method probably?
        }
    }

    /**
     * Author: Erin-Marie
     * adds an event to the users list of entered events
     * assumes that the entrant has already been approved to enter the event ie the addEntrant method of the event class has already been executed
     * called by the addEntrant() method of the Event class
     * @param event the Event being entered
     * TODO: need to update firebase
     * TESTME: test that the event is now in myEvents
     */
    public void addEvent(Event event) {
        this.myEvents.add(event);
    }

    /**
     * Author: Erin-Marie
     * removes an event to the users list of entered events
     * assumes that the entrant has already been removed from the events list of entrants
     * called by the removeEntrant() method of the Event class
     * @param event the Event being left
     * TODO: need to update firebase
     * TESTME: test that the event is no longer in myEvents
     */
    public void leaveEvent(Event event){
        this.myEvents.remove(event);
    }

    /**
     * Author: Erin-Marie
     * Returns false if the entrant is not entered in the events lottery, returns true if they are
     * @param event the event being checked if the user is entered in
     * TESTME: test that the event is returning correct bool
     */
    public Boolean checkIsEntrant(Event event){
        return this.myEvents.contains(event);
    }

    /**
     * Author: Erin-Marie
     * adds an event to the users list of organized events
     * checks that the event is hosted at the users facility, and is not already in their list of events, if not, returns 1
     * @param event the Event being created
     * TODO: need to update firebase
     * TESTME: test that the event is now in myOrgEvents
     *         test that the method returns 1 if the event facility is not the users facility
     */
    public Integer addOrgEvent(Event event) {
        if (event.getFacility() != myFacility | myOrgEvents.contains(event)) {
            return 1;
        }else{
            this.myOrgEvents.add(event);
            return 0;
        }

    }



}

