package com.example.myapplication.ui.notifications;

import static java.time.Instant.now;

import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

/**
 * Author: Erin-Marie
 * The class for notification objects
 */
public class Notification {
    String title;
    String message;
    String sender;
    ArrayList<DocumentReference> recipients;
    Timestamp sentTime;


    /**
     * Author: Erin-Marie
     * empty constructor for firebase
     */
    public Notification(){

    }
    /**
     * Author: Erin-Marie
     * Notification Constructor for a message from an event ending
     * @param message either "Congratulations you were selected, please view check the event for you invitation" or "Sorry, you were not selected"
     * @param recipients array of the document references for the entrants receiving the message
     *                   this is the winners list or the users list from the method that ends the event
     * @param event the event lottery that ended
     */
    public Notification(String message,  ArrayList<DocumentReference> recipients, Event event) {
        this.title = "Event Lottery Result";
        this.message = message;
        this.recipients = recipients;
        this.sender = event.getEventName();
        this.sentTime = new Timestamp(now());
    }

    /**
     * Author: Erin-Marie
     * Constructor for a custom message sent from an organizer to a user
     * @param title Subject line for the message
     * @param message the custom message the sender writes
     * @param recipients array of the document references for the entrants receiving the message
     *                   this could be a single entrant
     * @param senderUser the user profile who sent the message
     */
    public Notification(String title, String message, ArrayList<DocumentReference> recipients, UserProfile senderUser) {
        this.title = title;
        this.message = message;
        this.recipients = recipients;
        this.sender = senderUser.getFirstName();
        this.sentTime = new Timestamp(now());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<DocumentReference> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<DocumentReference> recipients) {
        this.recipients = recipients;
    }

    public String getSender() {
        return sender;
    }

    public void setUserSender(UserProfile sender) {
        this.sender = sender.getFirstName();
    }

    public void setEventSender(Event event) {
        this.sender = event.getEventName();
    }



    public Timestamp getSentTime() {
        return sentTime;
    }

    public void setSentTime(Timestamp sentTime) {
        this.sentTime = sentTime;
    }
}