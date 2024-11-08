package com.example.myapplication.database;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

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
    public Event event;
    public String eventID = null;
    public String uuid; //for when the current user adds an event

    public ArrayList<Event> myEvents = new ArrayList<Event>();
    public ArrayList<Event> myOrgEvents = new ArrayList<Event>();
    public ArrayList<UserProfile> losersList = new ArrayList<UserProfile>();
    public ArrayList<UserProfile> winnersList = new ArrayList<UserProfile>();
    public ArrayList<UserProfile> entrantsList = new ArrayList<UserProfile>();


    /**
     * Class constructor
     * @param connection DB connection declared in mainActivity
     */
    public EventDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.connection = connection;
        this.allEvents = connection.getAllEventsCollection();
        this.db = connection.getDB();
        this.uuid = connection.getUUID();
    }


    /**
     * Getters and setters
     */

    public ArrayList<Event> getMyEvents() {
        return myEvents;
    }

    public void setMyEvents(ArrayList<Event> myEvents) {
        this.myEvents = myEvents;
    }

    public ArrayList<Event> getMyOrgEvents() {
        return myOrgEvents;
    }

    public void setMyOrgEvents(ArrayList<Event> myEvents) {
        this.myOrgEvents = myOrgEvents;
    }

    public Event getEvent(){
        return this.event;
    }

    public ArrayList<UserProfile> getLosersList() {
        return losersList;
    }

    public ArrayList<UserProfile> getWinnersList() {
        return winnersList;
    }

    public ArrayList<UserProfile> getEntrantsList() {
        return entrantsList;
    }

    /**
     * Author: Erin-Marie
     * This is a method that manually ends an event lottery
     * @param event
     * TODO: make automated once the event lottery close date passes
     */
    public void endEvent(Event event){
        //Mark the event as being over
        event.setEventOver(Boolean.TRUE);
        //gets the events entrant list
        ArrayList<DocumentReference> entrantsList = event.getEntrantsList();
        //get the max participants for the event
        Integer participants = event.getMaxParticipants();

        //If there are actually enough entrants to fill the event
        if (participants < entrantsList.size()){
            getRandomWinners(entrantsList, participants, event);

        } else {
            //all the entrants are winners
            event.setWinnersList(entrantsList);
        }

        //update the event data in the db
        updateEvent(event);

        //send out the notifications to the entrants
        sendMessageToLosers(event);
        sendMessageToWinners(event);

    }

    /**
     * Author: Erin-Marie
     * @param entrants the entrants of the event
     * @param participants the number of users to be chosen to participate in the event
     * @param event the event to be attended
     */
    public void getRandomWinners(ArrayList<DocumentReference> entrants, int participants, Event event)
    {
        //Initialize random
        Random rand = new Random();

        //temporary list of winners
        ArrayList<DocumentReference> eventWinnersList = event.getWinnersList();

        for (int i = 0; i < participants; i++) {
            //use random indexes to chose winners
            int randomEntrantIndex = rand.nextInt(entrants.size());

            // add the winner to the winner list
            eventWinnersList.add(entrants.get(randomEntrantIndex));

            //remove the entrant from the entrants list
            entrants.remove(randomEntrantIndex);
        }
        //set the winners list
        event.setWinnersList(eventWinnersList);

        //The remaining users in entrants are the losers
        event.setLosersList(entrants);


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
     * @param docRef of the event of interest
     * @param listener onSuccess listener, returns null if the event does not already exist
     */
    public void checkEventExists(DocumentReference docRef, OnSuccessListener<DocumentSnapshot> listener) {
        // DocumentReference docRef = getEventDocumentRef(eventID); // get the document reference for the current users document from the AllUsers Collection
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d(TAG, "Event Exists");
                listener.onSuccess(snapshot);
            } else {
                Log.d(TAG, "Event does not Exist");
                listener.onSuccess(null);
            }
        });
    }

    /**
     * Author: Erin-Marie
     * Sets the current event of interest as a class attr to return it to activity that wanted to the event
     * this is definitley bad practice, but it is the solution for now
     * @param event the event object just created by getEvent, or it is null
     */
    public void returnEvent(Event event){
        this.event = event;
    }

    /**
     * Author: Erin-Marie
     * Gets an Event object instance for an eventID from the db
     * @param eventID of the event you need
     * @param onSuccessListener to handle the task
     * @return event object of the event
     */
    public Event getEvent(String eventID, OnSuccessListener<Event> onSuccessListener){
        //See if the event exists in the db
        DocumentReference docRef = getEventDocumentRef(eventID);
        checkEventExists(docRef, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                // event exists
                if (snapshot != null) {
                    Log.v(TAG, "Event fetched");
                    //make the event snapshot into an event object and return it
                    returnEvent(snapshot.toObject(Event.class));
                } else {
                    Log.v(TAG, "Event not exist");
                    //return null
                    returnEvent(null);
                }
            }
        });
        return this.event;
    }

    /**
     * Author: Erin-Marie
     * Gets an Event object instance for an eventID from the db
     * This is just a different signature of getEvent(String eventID, OnSuccessListener<Event> onSuccessListener)
     * @param docRef document reference of the event you need
     * @param onSuccessListener to handle the task
     * @return event object of the event
     */
    public Event getEvent(DocumentReference docRef, OnSuccessListener<Event> onSuccessListener){
        //See if the event exists in the db
        checkEventExists(docRef, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                // event exists
                if (snapshot != null) {
                    Log.v(TAG, "Event fetched");
                    //make the event snapshot into an event object and return it
                    returnEvent(snapshot.toObject(Event.class));
                } else {
                    Log.v(TAG, "Event not exist");
                    //return null
                    returnEvent(null);
                }
            }
        });
        return this.event;
    }

    /**
     * Author Erin-Marie
     * sets EventDB objects myEvents array to be all of the events the user has entered
     * for US.02.02.01
     * for US.02.06.03
     * @param user current user
     * @return this.myEvents but the return value isnt actually used
     */
    public ArrayList<Event> getUserEnteredEvents(UserProfile user){
        //query all events for the ones where the current user is an entrant
        Query query = allEvents.whereArrayContains("entrantsList", user.getDocRef()).orderBy("eventDate", Query.Direction.DESCENDING);
        getQuery(query, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //empty the current list of events so there are not duplicates
                ArrayList<Event> myEventsCol = new ArrayList<Event>();
                myEvents.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //add each event to the arraylist
                    myEvents.add(document.toObject(Event.class));
                    Log.v(TAG, "size: " + myEvents.size());
                }
                Log.v(TAG, "On complete getUserEvnteredEvents finsihed");
            }
        });
        //Log.v(TAG, "size: " + myEvents.size());
        return this.myEvents;

    }

    /**
     * Author Erin-Marie
     * sets EventDB objects myOrgEvents array to be all of the events the user has organized
     * @param user current user
     * @return this.myEvents but the return value isnt actually used
     */
    public ArrayList<Event> getUserOrgEvents(UserProfile user){
        //query all events for the ones where the current user is the organizer
        Query query = allEvents.whereArrayContains("organizerRef", user.getDocRef()).orderBy("eventDate", Query.Direction.DESCENDING);
        getQuery(query, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //empty the current list of notifs so there are not duplicates
                ArrayList<Event> myEventsCol = new ArrayList<Event>();
                myOrgEvents.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //add each event to the arraylist
                    myOrgEvents.add(document.toObject(Event.class));
                    Log.v(TAG, "size: " + myOrgEvents.size());
                }
            }
        });
        //Log.v(TAG, "size: " + myEvents.size());
        return this.myOrgEvents;
    }

    /**
     * Author Erin-Marie
     * sets EventDB objects losersList array to be all of the losers of the event
     * @param event that has been completed
     * no actual return, but will set this.losersList to contain all of the UserProfiles of the entrants that did not win the lottery
     */
    public void getEventLosers(Event event){
        //query for all losers documents of the users in the events losersList
        ArrayList<DocumentReference> losers = event.getLosersList();
        Query query = db.collection("allUsers").whereArrayContainsAny("losersList", losers);
        getQuery(query, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //empty the current list of losers so there are not duplicates
                ArrayList<Event> myEventsCol = new ArrayList<Event>();
                losersList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //add each user to the arraylist
                    losersList.add(document.toObject(UserProfile.class));
                    Log.v(TAG, "size: " + losersList.size());
                }

            }
        });
        //Log.v(TAG, "size: " + myEvents.size());
    }

    /**
     * Author Erin-Marie
     * sets EventDB objects entrants array to be all of the entrants of the event
     * @param event that has been created
     * no actual return, but will set this.entrantsList to contain all of the UserProfiles of the entrants that did not win the lottery
     */
    public void getEventEntrants(Event event){
        //query for all losers documents of the users in the events losersList
        ArrayList<DocumentReference> entrants = event.getEntrantsList();
        Query query = db.collection("allUsers").whereArrayContainsAny("entrantsList", entrants);
        getQuery(query, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //empty the current list of losers so there are not duplicates
                ArrayList<Event> myEventsCol = new ArrayList<Event>();
                entrantsList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //add each user to the arraylist
                    entrantsList.add(document.toObject(UserProfile.class));
                    //Log.v(TAG, "size: " + entrantsList.size());
                }
                Log.v(TAG, "Entrants list read from database");

            }

        });
    }

    /**
     * Author Erin-Marie
     * sets EventDB objects winnersList array to be all of the winners of the event
     * @param event that has been completed
     * @return this.losersList but the return value isnt actually used
     */
    public ArrayList<UserProfile> getEventWinners(Event event){
        //query for all the winners' documents of the users in the events winnersList
        ArrayList<DocumentReference> winners = event.getWinnersList();
        Query query = db.collection("allUsers").whereArrayContainsAny("winnersList", winners);
        getQuery(query, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //empty the current list of winners so there are not duplicates
                ArrayList<Event> myEventsCol = new ArrayList<Event>();
                winnersList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //add each user to the arraylist
                    winnersList.add(document.toObject(UserProfile.class));
                    Log.v(TAG, "size: " + winnersList.size());
                }

            }
        });
        //Log.v(TAG, "size: " + myEvents.size());
        return this.winnersList;
    }



    /**
     * Author: Erin-Marie
     * this function will get the documents from any query passed to it
     * does not return, but will give the listener argument the task back if it is successful
     * @param query Query object you want to execute
     * @param listener OnCompleteListener from calling method, calling method needs a
     */
    public void getQuery(Query query, OnCompleteListener<QuerySnapshot> listener){
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listener.onComplete(task);

                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Author: Erin-Marie
     *
     * @param docRef the document reference generated by Firebase
     *        event the event created
     *        paramters are null if db write was unsuccessful
     */
    public void returnEventID(DocumentReference docRef){
        if (event != null) {
            event.setEventID(docRef.getId());
            event.setDocRef(docRef);
            event.getOrganizer().addOrgEvent(event);
            updateEvent(event);
        } else {
            event.setEventID(null);
        }


    }

    /**
     * Author: Erin-Marie
     * adds a newUser to the AllUsers collection, using the current users uuid as the document id
     * stored as a custom object, which requires the public empty constructor in the UserProfile class
     * Sets the currentUser and userDocument Reference attributes of the UserDB instance
     * Reference: <a href="https://firebase.google.com/docs/firestore/manage-data/add-data?_gl=1">...</a>*189tp3e*_up*MQ..*_ga*MTI4NzA3MTQ3MC4xNzI5NzI3MjA0*_ga_CW55HF8NVT*MTcyOTcyNzIwNC4xLjAuMTcyOTcyNzIwNC4wLjAuMA..
     *
     * @return
     */
    public boolean addEvent(Event event){
        this.event = event;
        this.db.collection("AllEvents").add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        returnEventID(documentReference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        //returnEventID(null, null);
                    }
                });
        //return this.eventID;
        return false;
    }


    /**
     * Author: Erin-Marie
     * Sets the events docRef field, and updates the event document in the db
     * @param event to be updated
     */
    public void updateEvent(Event event){
        String eventID = event.getEventID();

        this.db.collection("AllEvents").document(eventID).set(event);

    }

    /**
     * Author: Erin-Marie
     * Adds the entrant to the entrantsList of the event, and updates the Event in the DB
     * @param event the event being entered
     * @return Boolean of the success of entering the event
     *         returns False if they could not be added becuase the waiting list is full
     *         returns True if they were added to the waiting list
     * assumed the entrant is the current user
     */
    public Boolean addEntrant(Event event){
        DocumentReference entrant = connection.getUserDocumentRef();
        Integer added = event.addEntrant(entrant);
        if (added == 0){
            Log.v(TAG, "Waiting list is full, user could not be added");
            return Boolean.FALSE;
        } else {
            updateEvent(event);
            return Boolean.TRUE;
        }

    }

    /**
     * Author Erin-Marie
     * Method to send the notifications to all losers when the event ends
     * @param event  the event that has ended
     */
    public void sendMessageToLosers(Event event){
        Notification forLosers = new Notification("You were not selected to participate in this event.", event.getLosersList(), event);
        connection.getNotifDB().addNotification(forLosers);
    }

    /**
     * Author Erim-Marie
     * Method to send the notifications to all winners when the event ends
     * @param event the event that has ended
     */
    public void sendMessageToWinners(Event event){
        Notification forWinners = new Notification("You were selected to participate in this event! Check the event listing to see your invitation.", event.getWinnersList(), event);
        connection.getNotifDB().addNotification(forWinners);
    }


}
