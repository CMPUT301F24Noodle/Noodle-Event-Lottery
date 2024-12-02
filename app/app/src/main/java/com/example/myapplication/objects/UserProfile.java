package com.example.myapplication.objects;

import static android.app.PendingIntent.getActivity;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Erin-Marie
 * Class for a user object, stores any profile data they choose to add, and has
 * method to get their UUID
 */
public class UserProfile implements Serializable {

    public String name;
    public String email;
    public String phoneNumber;
    public String address;
    public Integer privileges; // 0 is default, means they are just an entrant, 1 means they also have organizer privilege
    public DocumentReference docRef;
    public String uuid;
    public Boolean isAdmin; // true if the user has admin privileges
    public Boolean allowNotifs = Boolean.TRUE; // False if they do not want to receive notifications, True if they do allow notifications
    public  Boolean geoLocationOn; // True if they allow geoLocation, False if not
    public ArrayList<DocumentReference> myEvents; // the users ENTERED events
    public ArrayList<Event> myEnteredEvents; // the users organized events
    public ArrayList<DocumentReference> myNotifications;

    // Profile Picture Stuff
    public String encodedPicture;
    public Boolean hasProfilePic; // false if user has no profile pic and needs a generated one


    // organizer privilege attributes
    public ArrayList<DocumentReference> myOrgEvents; // the users ORGANIZED events
    public Facility facility; // the users facility they created, can only have one


    public UserProfile() {
    } // need for firebase

    /**
     * Author: Erin-Marie
     * UserProfile is initially created with ony default values, since the user is
     * not required to enter their profile information
     * If the user wants to edit anything, it will be done through the getter and
     * setter methods called from the ProfileActivity
     */
    public UserProfile(String uuid) {

        this.privileges = 0; // defaults to entrant privileges
        this.allowNotifs = Boolean.TRUE; // defaults to allow notifications
        this.geoLocationOn = Boolean.FALSE; // defaults to false, need to ask user for permission first
        this.isAdmin = Boolean.FALSE;
        this.uuid = uuid;
        this.myEvents = new ArrayList<DocumentReference>();
        this.myOrgEvents = new ArrayList<DocumentReference>();
        this.myNotifications = new ArrayList<DocumentReference>();
        this.myEnteredEvents = new ArrayList<Event>();
        this.hasProfilePic = Boolean.FALSE;

        // TODO: make a res file with a default profile picture to use until a user
        // submits their own
        // this.profilePicture =
    }


    public Facility getFacility() {
        return this.facility;
    }

    /**
     * Author: Erin-Marie
     * connects a facility instance to the user who created it
     * grants the user organizer privileges
     * 
     * @param facility Facility the user owns
     * @return 0 if the facility is successfully attached to the user, or 1 if not
     *         (user already has a facility)
     */
    public Integer setFacility(Facility facility) {
        // Check that the user does not already have a facility
        if (this.facility == null) {
            this.facility = facility;
            // creating a facility gives the user Organizer privileges, increase privileges
            this.privileges = 1;
            return 0; // return successful

        } else {
            return 1; // could not add facility, user already owns a facility
        }
    }

    /**
     * Author: Erin-Marie
     * checks that the facility is the users facility
     * removes the facility from the owners profile
     * demotes the users privileges to just entrant (0)
     * @param facility facility that is being deleted
     */
    public void removeFacility(Facility facility) {
        if (this.facility == facility) {
            this.facility = null;
            this.privileges = 0; // removing your facility means you are no longer an organizer

        }
    }

    /**
     * Author: Erin-Marie
     * adds an event to the users list of entered events
     * assumes that the entrant has already been approved to enter the event ie the
     * addEntrant method of the event class has already been executed
     * called by the addEntrant() method of the Event class
     * @param event the doc reference for the Event being entered
     */
    public void enterEvent(Event event) {
        myEvents.add(event.getDocRef());
    }

    /**
     * Author: Erin-Marie
     * removes an event to the users list of entered events assumes that the entrant has already been removed from the events list of entrants
     * called by the removeEntrant() method of the Event class
     * @param event the Event being left
     */
    public void leaveEvent(Event event) {
        this.myEvents.remove(event.getDocRef());
    }

    /**
     * Author: Erin-Marie
     * Returns false if the entrant is not entered in the events lottery, returns true if they are
     * @param event the event being checked if the user is entered in
     */
    public Boolean checkIsEntrant(Event event) {
        return this.myEvents.contains(event.getDocRef());
    }

    /**
     * Author: Erin-Marie
     * adds an event to the users list of organized events
     * checks that the event is hosted at the users facility
     * @param event the Event being created
     */
    public void addOrgEvent(Event event) {
        assert event != null;
        myOrgEvents.add(event.getDocRef());

    }

    //getters and setters

    public Boolean checkIsOrganizer(){return privileges == 1;}

    public void clearNotifs() {
        myNotifications = new ArrayList<DocumentReference>();
    }

    public void setEncodedPicture(String encodedBase64Picture) {
        this.encodedPicture = encodedBase64Picture;
    }
    public String getEncodedPicture(){
        return this.encodedPicture;
    }


    public void setMyEvents(ArrayList<DocumentReference> myEvents) {
        this.myEvents = myEvents;
    }

    public void setMyOrgEvents(ArrayList<DocumentReference> myOrgEvents) {
        this.myOrgEvents = myOrgEvents;
    }

    public ArrayList<DocumentReference> getMyNotifications() {
        return myNotifications;
    }

    public void setMyNotifications(ArrayList<DocumentReference> myNotifications) {
        this.myNotifications = myNotifications;
    }

    public ArrayList<Event> getMyEnteredEvents() {
        return myEnteredEvents;
    }

    public void setMyEnteredEvents(ArrayList<Event> myEnteredEvents) {
        this.myEnteredEvents = myEnteredEvents;
    }


    public DocumentReference getDocRef() {
        return docRef;
    }

    public void setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public String getUuid() {
        return uuid;
    }

    // QUESTION: do we put input validation here or within the EditUserProfile
    // fragment that will call these setters?
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String number) {
        this.phoneNumber = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getHasProfilePic(){return hasProfilePic;}

    public void setHasProfilePic(Boolean newHasProfilePic){ this.hasProfilePic = newHasProfilePic;}

    public Integer getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Integer privileges) {
        this.privileges = privileges;
    }

    public ArrayList<DocumentReference> getMyEvents() {
        return myEvents;
    }

    public ArrayList<DocumentReference> getMyOrgEvents() {
        return myOrgEvents;
    }

    public Boolean getAllowNotifs() {
        return allowNotifs;
    }

    public void setAllowNotifs(Boolean allowNotifs) {
        this.allowNotifs = allowNotifs;
    }

    // MAYBE: if geoLocation is stored within sharedPreferences this will need to be
    // more complex
    public Boolean getGeoLocationOn() {
        return geoLocationOn;
    }

    // MAYBE: if geoLocation is stored within sharedPreferences this will need to be
    // more complex
    public void setGeoLocationOn(Boolean geoLocationOn) {
        this.geoLocationOn = geoLocationOn;
    }

    public void setIsAdmin(Boolean b) {
        this.isAdmin = b;
        this.setAdmin(b);
    }
}
