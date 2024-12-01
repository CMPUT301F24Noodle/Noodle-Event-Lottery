package com.example.myapplication.objects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.example.myapplication.BitmapHelper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Event Class
 * Author: Erin-Marie
 * Purpose: Event object contains all information and methods for an event an
 * organizer creates
 * TODO make methods for ending the contest and selecting winners
 * make method for notifying winners and losers and sending their invitations
 * make method for making the list of confirmed attendants
 * make method for selecting a new winner if someone rejects an invitation
 * make method for organizer to send a notification with custom message to all
 */
@IgnoreExtraProperties // Ignore extra properties from Firebase
public class Event implements Serializable {
    // Other class objects connected to the event
    public Facility facility; // Facility where the event is held
    public UserProfile organizer; // User who organized the event
    public DocumentReference organizerRef;

    // For the details of the event
    public String eventName;
    public String eventID;
    public DocumentReference docRef;
    public String eventPoster; // URL or base64 string for the event poster image
    public Date eventDate; // date the actual event will occur
    public String contact; // this is a phone number
    public String eventTime; // Sam: I added this variable, not sure what type it should be
    public Integer maxEntrants; // -1 if organizer does not want to restrict capacity
    public Integer maxParticipants;
    public Boolean geoLocation; // False if organizer does not require entrants to have geoLocation on
    public String eventDetails;
    private String eventLocation;
    private int waitingListSize;
    // QRCODE STUFF
    private Bitmap QRCode; // the bitmap of the QR code
    private String HashedString; // TODO for part 4, do stuff with hash

    // For status of the lottery
    public Date lotteryCloses; // date winners will be selected and notified
    public Boolean eventOver; // False until the event attendance list is finalized, or the eventDate has
    public Boolean eventFull; // False if there is still room for entrants, or if maxEntrants == -1

    public ArrayList<DocumentReference> entrantsList; // list of all entrants, by document reference
    public ArrayList<DocumentReference> winnersList; // list of all users who won the lottery, may have max length equal
                                                     // to
    public ArrayList<DocumentReference> losersList; // list of all users who lost the lottery
    public ArrayList<DocumentReference> acceptedList; // list of all users who have accepted their invitation
    public ArrayList<DocumentReference> declinedList; // list of all users who have accepted their invitation



    // Editor: Sam
    // No-arg constructor for Firebase
    public Event() {
        // Initialize lists to avoid null references
        this.entrantsList = new ArrayList<DocumentReference>(); // have to intialize so .size() wont return null
        this.winnersList = new ArrayList<DocumentReference>(); // have to intialize so .size() wont return null
        this.losersList = new ArrayList<DocumentReference>(); // have to intialize so .size() wont return null
        this.acceptedList = new ArrayList<DocumentReference>();
        this.declinedList = new ArrayList<DocumentReference>();
    }

    /**
     * Class constructor
     * 
     * @param facility      the facility the event is hosted at, cannot be null
     * @param organizer     the user profile of the event creator, cannot be null
     * @param eventName     the name of the event
     * @param eventPoster   the poster for the event
     * @param eventDate     the data the event occurs
     * @param maxEntrants   the max number of entrants they are allowing
     * @param lotteryCloses the day the lottery closes
     * @param geoLocation   whether they want geoLocation to be required
     * @throws WriterException this is for the QR code generation
     */
    public Event(Facility facility, UserProfile organizer, String eventName, String eventPoster, Date eventDate,
            String eventDetails, String contact,
            Integer maxEntrants, Integer maxParticipants, Date lotteryCloses, Boolean geoLocation)
            throws WriterException {
        this.facility = facility;
        this.organizer = organizer;
        this.organizerRef = organizer.getDocRef();
        this.eventName = eventName;
        this.eventPoster = eventPoster; // can be null
        this.eventDate = eventDate; // event date is converted to a date when the input is taken
        this.eventTime = eventTime;
        this.eventDetails = eventDetails;
        this.contact = contact;
        this.maxEntrants = maxEntrants;
        this.maxParticipants = maxParticipants;
        this.lotteryCloses = lotteryCloses;
        this.geoLocation = geoLocation;
        this.eventOver = Boolean.FALSE;
        this.eventFull = Boolean.FALSE; // event capacity cannot be 0, so it is always false at init
        this.entrantsList = new ArrayList<DocumentReference>(); // have to intialize so .size() wont return null
        this.winnersList = new ArrayList<DocumentReference>(); // have to intialize so .size() wont return null
        this.losersList = new ArrayList<DocumentReference>(); // have to intialize so .size() wont return null
        this.acceptedList = new ArrayList<DocumentReference>();
        this.declinedList = new ArrayList<DocumentReference>();
        this.docRef = null;


        if (this.eventID != null) {
            this.QRCode = generateQRCode(eventID, 200, 200);
        }

    }

    /**
     * Author: Erin-Marie
     * Called anytime an entrant is added or removed from the entrants list
     * Updates whether the event entrant limit has been reached
     * TESTED: tested in EventTests.java testAddEntrant()
     * if capacity is already maxed,
     * if capacity is 1, then add new entrant, now check that it is returning maxed
     */
    public void setEventFull() {
        if (this.maxEntrants == -1 || this.maxEntrants > this.entrantsList.size()) {
            this.eventFull = Boolean.FALSE;
        } else {
            this.eventFull = Boolean.TRUE;
        }
    }

    /**
     * Author: Erin-Marie
     * Checks whether a user has accepted their invitation for an event yet
     *
     * @param event the event being checked
     * @param user  the user of interest
     * @return 0 if the event lottery has not ended
     *          1 if the user has already responded to their invitation
     *          2 if the user has not accepted their invitatin
     *          3 if the user did not recieve an invitation
     * have
     */
    public int hasAccepted(Event event, DocumentReference user) {
        if (event.eventOver == Boolean.FALSE){ //the event has not ended yet
            return 0;
        }
        //have not responded to invitation
        if (event.getWinnersList().contains(user)) {
                Log.v("Event", "User has not accepted their invitiation");
                return 2;
        } else if (event.getAcceptedList().contains(user) || event.getDeclinedList().contains(user)){
            Log.v("Event", "User has already responded to their invitiation");
            return 1;

        } else {
            Log.v("Event", "user was not selected for the event");
            return 3;
        }

    }

    /**
     * Author: Erin-Marie
     * Adds an entrant to the list of entrants for the event
     * Checks that the entrant is not already in the list of entrants
     *
     * @param entrant UserProfile that wants to enter event lottery
     * @return 1 if the user was added to the entrant list, or 0 if not
     *         calling function needs to add the event to the users myevents list,
     *         dependent on the return value of addEntrant
     *         if capacity it maxed, should return 0
     *         if capacity is not maxed, should return 1 and check that the entrant
     *         is now in the entrantsList
     */
    public int addEntrant(DocumentReference entrant) {
        if (!this.entrantsList.contains(entrant) && this.eventFull == Boolean.FALSE
                && this.eventOver == Boolean.FALSE) {
            this.entrantsList.add(entrant);
            // entrant.addEvent(this); //add the event to the entrants list of events
            setEventFull(); // update whether the event is full
            return 1;
        }
        return 0;
    }

    /**
     * Author: Erin-Marie
     * Adds an entrant to the list of entrants for the event
     * Checks that the entrant is not already in the list of entrants
     *
     * @param entrant UserProfile that wants to un-enter the event lottery
     *                TODO: needs to update firebase db
     *                TESTME: check that entrant is actually removed from the
     *                entrantList
     */
    public void removeEntrant(DocumentReference entrant) {
        this.entrantsList.remove(entrant);
        // entrant.leaveEvent(this.getEventID()); // remove the event from the entrants
        // list of events
        setEventFull(); // update whether the event is full
    }


    /**
     * Author: Xavier Salm
     * getter for the QR code
     */
    public Bitmap getQRCode() {
        return QRCode;
    }

    /**
     * Author: Xavier Salm
     * Generates a QR code for the event based on an input string
     */
    public Bitmap generateQRCode(String QRText, int width, int height) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter(); // create the thing that will encode the string

        BitMatrix bitMatrix = writer.encode(QRText, BarcodeFormat.QR_CODE, width, height); // create a matrix of bits,
                                                                                           // where the bit in each cell
                                                                                           // determines if that pixel
                                                                                           // should be black or white

        // TODO unit test that makes sure that width and height are equal to width and
        // height of the matrix

        // creates an empty QR code with the dimensions of bitMatrix
        // the Bitmap.Config.RGB_565 is used because the QR code just needs to be black
        // and white, and doesn't need a lot of colors
        // basically, RGB_565 determines what colors can be assigned to which pixels
        Bitmap QRCode = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // now fill out the bitmap to match the assignments of bitMatrix by going
        // through each pixel
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                // for each pixel, set the pixel to white if its true in the bitmatrix,
                // otherwise set it to white
                int color;
                if (bitMatrix.get(row, col)) {
                    color = Color.WHITE;
                } else {
                    color = Color.BLACK;
                }
                QRCode.setPixel(row, col, color); // actually set the color for that pixel
            }
        }
        this.QRCode = QRCode;
        return QRCode;
    }

    /**
     * Calculates how many new users need to be selected to fill the event, and returns the value
     * @return int max users that can be added to the event
     */
    public int getUsersNeededCount(){
        int count = maxParticipants - winnersList.size() - acceptedList.size();
        return Math.min(count, losersList.size());
    }

    // getters and setters

    public void setEntrantsList(ArrayList<DocumentReference> entrantsList) {
        this.entrantsList = entrantsList;
    }

    public void setWinnersList(ArrayList<DocumentReference> winnersList) {
        this.winnersList = winnersList;
    }

    public void setLosersList(ArrayList<DocumentReference> losersList) {
        this.losersList = losersList;
    }


    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }




    public DocumentReference getOrganizerRef() {
        return organizerRef;
    }

    public void setOrganizerRef(DocumentReference organizerRef) {
        this.organizerRef = organizerRef;
    }

    public Boolean getGeoLocation() {
        return geoLocation;
    }

    public int getWaitingListSize() {
        return waitingListSize;
    }

    public void setWaitingListSize(int waitingListSize) {
        this.waitingListSize = waitingListSize;
    }

    public void setGeoLocation(Boolean geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void setQRCode(Bitmap QRCode) {
        this.QRCode = QRCode;
    }

    public void setEventFull(Boolean eventFull) {
        this.eventFull = eventFull;
    }

    public String getHashedString() {
        return HashedString;
    }

    public void setHashedString(String hashedString) {
        HashedString = hashedString;
    }

    // Getters and Setters
    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public void setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
    }

    public DocumentReference getDocRef() {
        return this.docRef;
    }

    @Nullable // Sam: added Nullable for testing purpose
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

    public String getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    @Nullable // Sam: added Nullable for testing purpose
    public Date getEventDate() {
        try {
            String test = eventDate.toString();
        } catch (NullPointerException e) {
            return null;
        }

        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
        setEventFull(); // if they choose to add a capacity, update whether the event is already full.
        // No entrants are removed if the event is already over the capacity.
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

    public ArrayList<DocumentReference> getEntrantsList() {
        return this.entrantsList;
    }

    public ArrayList<DocumentReference> getWinnersList() {
        return this.winnersList;
    }

    public ArrayList<DocumentReference> getLosersList() {
        return this.losersList;
    }

    public String getEventID() {
        return this.eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public Boolean getEventFull() {
        return this.eventFull;
    }

    /**
     * @return returns count of how many users have entered the event lottery
     */
    public Integer countEntrants() {
        return this.entrantsList.size();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setAcceptedList(ArrayList<DocumentReference> acceptedList) {
        this.acceptedList = acceptedList;
    }

    public void setDeclinedList(ArrayList<DocumentReference> declinedList) {
        this.declinedList = declinedList;
    }

    public ArrayList<DocumentReference> getAcceptedList() {
        return acceptedList;
    }

    public ArrayList<DocumentReference> getDeclinedList() {
        return declinedList;
    }

    public double getLatitude() {
        return 53.0;
    }

    public double getLongitude() {
        return -113.0;
    }

    public Bitmap generatePoster(){
        if(eventPoster == null){
            return null;
        }
        BitmapHelper helper = new BitmapHelper();
        return helper.decodeBase64StringToBitmap(eventPoster);
    }
}