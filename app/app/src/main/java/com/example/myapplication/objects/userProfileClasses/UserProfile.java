package com.example.myapplication.objects.userProfileClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;

import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Erin-Marie
 * Class for a user object, stores any profile data they choose to add, and has
 * method to get their UUID
 * TODONE: connect UserProfile to db, to get the users db, and their collection
 * TODO: need to initialize the array attributes
 */
public class UserProfile implements Serializable {

    String name;
    String email;
    String phoneNumber;
    String address;
    Image profilePicture;
    Integer privileges; // 0 is default, means they are just an entrant, 1 means they also have
                        // organizer privilege
    DocumentReference docRef;
    ArrayList<DocumentReference> myEvents; // the users ENTERED events
    String uuid;

    // organizer privilege attributes
    ArrayList<DocumentReference> myOrgEvents; // the users ORGANIZED events
    Facility facility; // the users facility they created, can only have one

    ArrayList<DocumentReference> myNotifications;

    Boolean isAdmin; // true if the user has admin privileges

    // QUESTION: should these be within sharedPreferences, not stored within the
    // UserProfile itself?
    Boolean allowNotifs = Boolean.TRUE; // False if they do not want to receive notifications, True if they do allow
                                        // notifications
    Boolean geoLocationOn; // True if they allow geoLocation, False if not

    public ArrayList<Event> myEnteredEvents;

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

        this.name = "Name";
        this.email = "Email";
        this.privileges = 0; // defaults to entrant privileges
        this.allowNotifs = Boolean.TRUE; // defaults to allow notifications
        this.geoLocationOn = Boolean.FALSE; // defaults to false, need to ask user for permission first
        this.isAdmin = Boolean.FALSE;
        this.uuid = uuid;
        this.myEvents = new ArrayList<DocumentReference>();
        this.myOrgEvents = new ArrayList<DocumentReference>();
        this.myNotifications = new ArrayList<DocumentReference>();
        this.myEnteredEvents = new ArrayList<Event>();

        // TODO: make a res file with a default profile picture to use until a user
        // submits their own
        // this.profilePicture =
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

    public Image getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

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
     *         TESTME: test that the facility has been set
     *         test that if the user already has a facility, that the facility is
     *         not being overwritten
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
     * 
     * @param facility facility that is being deleted
     *                 TODO: update the users profile in firebase db
     *                 TESTME: test that the facility has been removed from the
     *                 users profile
     *                 test that the user now only has entrant privileges
     */
    public void removeFacility(Facility facility) {
        if (this.facility == facility) {
            this.facility = null;
            this.privileges = 0; // removing your facility means you are no longer an organizer
            // MAYBE: need to delete all events that they were hosting at this facility?
            // deleting the facility from the db will be done by the calling method
            // probably?
        }
    }

    /**
     * Author: Erin-Marie
     * adds an event to the users list of entered events
     * assumes that the entrant has already been approved to enter the event ie the
     * addEntrant method of the event class has already been executed
     * called by the addEntrant() method of the Event class
     * 
     * @param event the doc reference for the Event being entered
     *              TODO: need to update firebase
     *              TESTME: test that the event is now in myEvents
     */
    public void enterEvent(Event event) {
        myEvents.add(event.getDocRef());
    }

    /**
     * Author: Erin-Marie
     * removes an event to the users list of entered events
     * assumes that the entrant has already been removed from the events list of
     * entrants
     * called by the removeEntrant() method of the Event class
     * 
     * @param event the Event being left
     *              TESTME: test that the event is no longer in myEvents
     */
    public void leaveEvent(Event event) {
        this.myEvents.remove(event.getDocRef());
    }

    /**
     * Author: Erin-Marie
     * Returns false if the entrant is not entered in the events lottery, returns
     * true if they are
     * 
     * @param event the event being checked if the user is entered in
     *              TESTME: test that the event is returning correct bool
     */
    public Boolean checkIsEntrant(Event event) {
        return this.myEvents.contains(event.getDocRef());
    }

    /**
     * Author: Erin-Marie
     * adds an event to the users list of organized events
     * checks that the event is hosted at the users facility, and is not already in
     * their list of events, if not, returns 1
     * 
     * @param event the Event being created
     *              TESTME: test that the event is now in myOrgEvents
     *              test that the method returns 1 if the event facility is not the
     *              users facility
     */
    public void addOrgEvent(Event event) {
        assert event != null;
        myOrgEvents.add(event.getDocRef());

    }

    public void clearNotifs() {
        myNotifications = new ArrayList<DocumentReference>();
    }

    /**
     * Author: Xavier Salm
     * generates a profile picture based on the first character of this users username
     * so a user with the username "steve" would have a profile picture that is just 's'
     *
     * @return profilePic: the newly generated profile picture
     */
    public Bitmap generateProfilePicture(){
        // get the character for the picture
        String firstChar = Character.toString(getName().charAt(0));

        // set the dimensions of the picture
        // TODO: what should the dimensions be?
        int width = 100;
        int height = 100;

        // TODO: could change the config so that the profile picture has color
        // create the bitmap that will become the new profile picture
        Bitmap profilePic = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // get a canvas to draw on the bitmap
        Canvas canvas = new Canvas(profilePic);

        // create the paint for the background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE); // TODO this color can be changed for UI

        // set the entire bitmap to a background color (because its not allowed to be transparent)
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // now that the canvas is painted, we can actually draw on it
        Paint charPaint = new Paint();
        charPaint.setColor(Color.BLACK); // TODO this color can be changed for UI
        charPaint.setTextSize(50); // TODO this size can be changed for UI

        // need to adjust height and width because the canvas uses pixels while height and width are integers
        // TODO make the text centered
        float textWidth = charPaint.measureText(firstChar);
        float adjustedWidth = (width - textWidth) / 2; // Center horizontally
        float adjustedHeight = (height - (charPaint.descent() + charPaint.ascent())) / 2;

        // paint the string onto the canvas!
        canvas.drawText(firstChar, adjustedWidth, adjustedHeight, charPaint);

        return profilePic;
    }

}
