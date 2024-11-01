package com.example.myapplication.database;


import android.util.Log;

import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Author: Erin-Marie
 * class that does all database operations for UserProfile objects
 */
public class EventDB {
    //For logcat
    private final static String TAG = "EventDB";

    public DBConnection connection;
    public CollectionReference allEvents;
    public FirebaseFirestore db;
    public Event event = null;

    public EventDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.connection = connection;
        this.allEvents = connection.getAllEventsCollection();
        this.db = connection.getDB();
    }

    /**
     * Author: Erin-Marie
     * @param eventID the string id of the event
     * @return eventRef DocumentReference of the event document in AllEvents collection
     */
    public DocumentReference getEventDocumentRef(String eventID){
        DocumentReference eventRef = this.db.collection("AllEvents").document("Event" + eventID);
        return eventRef;
    }

    /**
     * Author: Erin-Marie
     * Checks if the event exists in the database
     * @param eventID the eventID of the event of interest
     * @param listener onSuccess listener, returns null if the user does not already exist
     */
    public void checkEventExists(String eventID, OnSuccessListener<DocumentSnapshot> listener) {
        DocumentReference docRef = getEventDocumentRef(eventID); // get the document reference for the current users document from the AllUsers Collection
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d(TAG, "Event Exists" + eventID);
                listener.onSuccess(snapshot);
            } else {
                Log.d(TAG, "Event does not Exist" + eventID);
                listener.onSuccess(null);
            }
        });
    }

    public void returnEvent(Event event){
        this.event = event;
    }

    public Event getEvent(String eventID){
        checkEventExists(eventID, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot != null) { // user is already in the database
                    Log.v(TAG, "Event fetched");
                    returnEvent(snapshot.toObject(Event.class));
                } else {
                    Log.v(TAG, "Event not exist");
                    returnEvent(null);
                }
            }
        });
        return this.event;
    }
}
