package com.example.myapplication.database;

import com.google.firebase.firestore.DocumentReference;

/**
 * Author Erin-Marie
 *
 *
 * Class that controls db operations for facility objects
 * A facility document is identified by "Facility" + UUID of the the owner
 * A facility has an "HostedEvents" of document references for all events hosted at that facility
 */
public class FacilityDB {
    //For logcat
    private final static String TAG = "FacilityDB";

    //reference to the current users facility document in the AllUsers collection
    private final DocumentReference facilityDocument;

    public FacilityDB(DBConnection connection) {
        //Gets a reference to the current users facility document in the db
        this.facilityDocument = connection.getFacilityDocument();
    }

    public void getHostedEvents(){
        this.facilityDocument.collection("HostedEvents");
    }
}
