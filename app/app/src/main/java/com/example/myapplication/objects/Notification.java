package com.example.myapplication.objects;

import java.util.Date;

/**
 * Author: Erin-Marie
 * Class for each notification sent to a user.
 * Used for lottery results, and manual messages from organizer
 * sendNotification method is a part of a different activity, and does not need to be a method of the notification class
 */
public class Notification {

    UserProfile sender; //user that send the message
    UserProfile recipient; //user that receives the message
    String message; //content of the message
    Date sentDate; //date the message was sent

    /**
     * Author: Erin-Marie
     * Constructor method for Notification Class
     * @param sender user that send the message
     * @param recipient user that receives the message
     * @param message content of the message
     * TODO: updated firebase db
     */
    public Notification(UserProfile sender, UserProfile recipient, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.sentDate = new Date(); //date message is created. app will not have notification drafts, it is either sent or discarded.
    }

    //Only getters
    public UserProfile getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public UserProfile getSender() {
        return sender;
    }

    public Date getSentDate() {
        //Will return in form: dayofweek month day hours:minutes:seconds: IST year
        return sentDate;
    }

}
