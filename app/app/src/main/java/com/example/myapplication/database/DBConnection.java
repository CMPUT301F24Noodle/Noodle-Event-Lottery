package com.example.myapplication.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

/**
 * Author: Erin-Marie
 * DBConnector class connects to the Firestore Firebase Database where app data will be stored
 * an Instance of this class is a Connection to the Noodle-Event-Lottery firestore database.
 * To access any collection of the database, use the respective <object>DB class (UserDB, EventDB, FacilityDB)
 * Reference: <a href="https://firebase.google.com/docs/android/setup#:~:text=Open%20the%20Firebase%20Assistant%3A%20Tools,your%20Android%20project%20with%20Firebase">...</a>.
 */
public class DBConnection {

    /**
     * The Noodle-Event-Lottery Database has the following structure:
     * 3 Base Collections: AllEvents, AllFacilities, AllUsers
     * AllUsers is a collection of documents, each document represents one user, and is identified as "User" + uuid
     *      Contains all fields that a UserProfile class instance has
     *      The arrays for MyEnteredEvents and MyOrganizedEvents are arrays of references to Event documents of the AllEvents collection
     * AlLEvents is a collection of documents, each document represents one Event, and is identified as by a unique FID generated by firebase
     * AllFacilities is a collection of documents, each document represents on Event, and is identified by "Facility" + uuid of the creator
     * <a href="https://console.firebase.google.com/u/0/project/noodle-event-lottery/firestore/databases/-default-/data/~2FAllUsers~2FUser">...</a>
     */

    private FirebaseFirestore db; //firebase instance
    private String uuid; //Store the users ID
    public UserProfile user;
    private static final String TAG = "DBConnection"; //Database ID
    public UserDB userDB;
    public NotificationDB notifDB;
    public EventDB eventDB;
    public FacilityDB facilityDB;



    /**
     * Constructor method for class.
     * sets the db instance, stores the users UUID, logs the UUID value
     * @param context
     */
    public DBConnection(Context context) {
        FirebaseApp.initializeApp(context);
        this.db = FirebaseFirestore.getInstance();
        this.uuid = genUUID(context); //get UUID the first time
        userDB = new UserDB(this);
        eventDB = new EventDB(this);
        facilityDB = new FacilityDB(this);
        notifDB = new NotificationDB(this);

        Log.v(TAG, "UUID: " + uuid); //for debugging purpose, keeps track of UUID value in logcat
    }

    public UserDB getUserDB() {
        return userDB;
    }

    public NotificationDB getNotifDB() {
        return notifDB;
    }

    public EventDB getEventDB() {
        return eventDB;
    }

    public FacilityDB getFacilityDB() {
        return facilityDB;
    }


    /**
     * Author: Erin-Marie
     * Getter method for the FirebaseFirestore instance, getter for the DataBase
     * @return database instance
     */
    public FirebaseFirestore getDB() {
        return this.db;
    }

    /**
     * @return uuid, returns the string UUID of the current user
     */
    public String getUUID(){
        return this.uuid;
    }

    /**
     * Sets the user attr with the current users profile
     * @param user current user
     */
    public void setUser(UserProfile user){
        this.user = user;
    }

    public UserProfile getUser(){ return this.user; }
    /**
     * Author: Erin-Marie
     * Method to get the users unique UUID, or generate one if they do not have one
     * This was in UserProfile and i moved it to here
     * Citation: <a href="https://developer.android.com/training/data-storage/shared-preferences">...</a>
     * @param context which will provide the calling activity
     * @return strUUID, which is the users UUID in string form
     * TESTME
     */
    protected String genUUID(Context context) {
        //SharedPreferences is for storing data in key-value pairs in a local XML
        SharedPreferences sharedPreferences;
        //get info from shared preference file, using MODE_PRIVATE to only access the shared preference from the calling activity
        sharedPreferences = context.getApplicationContext().getSharedPreferences("preference", Context.MODE_PRIVATE);
        //get the UUID as a key-value pair, using "UUID" as the key
        String strUuid = sharedPreferences.getString("UUID", null);
        // if they don't have a UUID need to make one and store it
        if (strUuid == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            strUuid = UUID.randomUUID().toString();
            editor.putString("UUID", strUuid);
            editor.apply();
        }
        return strUuid;
    }

    /**
     * Author: Erin-Marie
     * getUserDocument() retrieves a reference to the current users document of the AllUsers collection of the DB
     * @return returns a DocumentReference to the Document associated with the current users UUID
     * TESTME
     */
    public DocumentReference getUserDocumentRef() {
        //CollectionReference doc = this.db.collection("AllUsers").document("User" + uuid).collection(subCollection);
        DocumentReference docRef = this.db.collection("AllUsers").document("User" + uuid);
        return docRef;
    }

    /**
     * Author: Erin-Marie
     * This method is used when you are trying to obtain a User Document for a user other than the current user
     * getUserDocument(String uuid) is a second form of getUserDocumentRef() that retrieves a reference to the given users document of the AllUsers collection of the DB
     * @param uuid the uuid of the user document you wish to obtain
     * @return returns a DocumentReference to the Document associated with the current users UUID
     * TESTME
     */
    public DocumentReference getUserDocumentRef(String uuid){
        DocumentReference docRef = this.db.collection("AllUsers").document("User" + uuid);
        return docRef;
    }

    /**
     * Author Erin-Marie
     * gets the document snapshot from the db
     * @param docRef of any document
     * @param listener for handling task
     */
    public void getDocumentFromReference(DocumentReference docRef, OnSuccessListener<DocumentSnapshot> listener){
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d(TAG, "document exists");
                listener.onSuccess(snapshot);
            } else {
                Log.d(TAG, "document does not exist");
                listener.onSuccess(null);
            }
        });
    }



    /**
     * returns the document reference for the event with the given eventID
     * @param eventID event id of interest
     * @return docRef for the event
     */
    public DocumentReference getEventDocumentRef(String eventID){
        DocumentReference docRef = this.db.collection("AllEvents").document(eventID);
        return docRef;
    }

    /**
     * returns the document reference for the event with the given eventID
     * @param event event object of interest
     * @return docRef for the event
     */
    public DocumentReference getEventDocumentRef(Event event){
        String eventID = event.getEventID();
        return getEventDocumentRef(eventID);
    }



    /**
     * Author: Erin-Marie
     * getFacilityDocument() retrieves a reference to the users facility
     * @return returns a DocumentReference to the Facility associated with the current UUID
     * The document contains the Facility information
     * TESTME
     */
    public DocumentReference getFacilityDocument() {
        //CollectionReference doc = this.db.collection("AllUsers").document("User" + uuid).collection(subCollection);
        DocumentReference doc = this.db.collection("AllFacilities").document("Facility" + uuid);
        return doc;
    }

    /**
     * Author: Erin-Marie
     * getAllFacilitiesCollection() retrieves a reference to the AllFacilitiesCollection
     * @return returns a DocumentReference
     * TESTME
     */
    public CollectionReference getAllFacilitiesCollection(){
        return this.db.collection("AllFacility");
    }



    /**
     * Author: Erin-Marie
     * getAllUsersCollection() retrieves a reference to the AllUsersCollection
     * @return returns a DocumentReference
     * TESTME
     */
    public CollectionReference getAllUsersCollection(){
        return this.db.collection("AllUsers");
    }


    /**
     * Author: Erin-Marie
     * getAllEventsCollection() retrieves a reference to the AllEventsCollection
     * @return returns a DocumentReference to the AllEvents collection
     * TESTME
     */
    public CollectionReference getAllEventsCollection(){
        return this.db.collection("AllEvents");
    }


    /**
     * Author: Erin-Marie
     * getAllNotificationsCollection() retrieves a reference to the AllUsersCollection
     * @return returns a DocumentReference to the user associated with the current UUID
     * The document contains the Facility information
     * TESTME
     */
    public CollectionReference getAllNotificationsCollection(){
        return this.db.collection("AllNotifications");
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
}
