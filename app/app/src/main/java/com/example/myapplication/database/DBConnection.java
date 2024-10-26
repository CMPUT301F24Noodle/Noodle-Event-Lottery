package com.example.myapplication.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * Author: Erin-Marie
 * DBConnector class connects to the Firestore Firebase Database where app data will be stored
 * Gets the current Users ID and connects to their collection
 * Reference: <a href="https://firebase.google.com/docs/android/setup#:~:text=Open%20the%20Firebase%20Assistant%3A%20Tools,your%20Android%20project%20with%20Firebase">...</a>.
 * QUESTION: Does the user need to authenticate with firebase, or do we just yoink their data for them?
 */
public class DBConnection {

    private FirebaseFirestore db; //firebase instance
    private String uuid; //Store the users ID
    private static final String TAG = "DBConnection"; //Database ID

    /**
     * Constructor method for class.
     * sets the db instance, stores the users UUID, logs the UUID value
     * @param context
     */
    public DBConnection(Context context) {
        FirebaseApp.initializeApp(context);
        this.db = FirebaseFirestore.getInstance();
        this.uuid = genUUID(context); //get UUID the first time
        Log.v(TAG, "UUID: " + uuid); //for debugging purpose, keeps track of UUID value in logcat
    }


    /**
     * Author: Erin-Marie
     * Getter method for the FirebaseFirestore instance, getter for the DataBase
     * @return database instance
     */
    public FirebaseFirestore getDB() {
        return this.db;
    }

    public String getUUID(){
        return this.uuid;
    }


    /**
     * Author: Erin-Marie
     * Method to get the users unique UUID
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
     * getUserDocument() retrieves a reference to the users document of the AllUsers collection of the DB
     * @return returns a DocumentReference to the Document associated with the current UUID
     * The document contains the users information, as well as an array of their entered events and organized events.
     * TESTME
     */
    public DocumentReference getUserDocument() {
        //CollectionReference doc = this.db.collection("AllUsers").document("User" + uuid).collection(subCollection);
        DocumentReference doc = this.db.collection("AllUsers").document("User" + uuid);
        return doc;
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



    //TODO: make a method to get the allusers collection
    //reference to AllUsers collection
    //private CollectionReference allUsersCollection;
}
