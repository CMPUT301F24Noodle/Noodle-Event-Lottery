package com.example.myapplication.database;

import static android.app.PendingIntent.getActivity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.MainActivity;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public DBConnection connection;
    public CollectionReference allFacilities;
    public FirebaseFirestore db;
    public Event event = null;
    public String uuid; //for when the current user adds an event

    //reference to the current users facility document in the AllUsers collection
    private final DocumentReference facilityDocument;

    /**
     * Facility class constructor
     * @param connection dbConnection from MainActivity
     */
    public FacilityDB(DBConnection connection) {
        //Gets a reference to the current users facility document in the db
        this.facilityDocument = connection.getFacilityDocument();
        this.connection = connection;
        this.allFacilities = connection.getAllFacilitiesCollection();
        this.db = connection.getDB();
        this.uuid = connection.getUUID();
    }


    /**
     * Author: Erin-Marie
     * adds the given facility object to the db under AllFacilities
     * TODO: see if it sets the facility as being the users
     * @param facility new facility
     */
    public void addFacility(Facility facility){
        this.db.collection("AllFacilities").document("Facility" + this.uuid).set(facility);
        //UserProfile user = facility.getOwner();
        //user.setFacility(facility);


    }



}
