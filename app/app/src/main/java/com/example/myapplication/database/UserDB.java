package com.example.myapplication.database;

import android.util.Log;

import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.UUID;

/**
 * Author: Erin-Marie
 * class that does all database operations for UserProfile objects
 * QUESTION: where is the instance of the UserDB that will call these methods?
 */
public class UserDB {

    //For logcat
    private final static String TAG = "UserDB";

    //reference to the current users document in the AllUsers collection
    public DocumentReference userDocument;
    public DBConnection storeConnection;
    public CollectionReference allUsers;
    public FirebaseFirestore db;
    public String uuid;

    public UserDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.storeConnection = connection;
        this.allUsers = connection.getAllUsersCollection();
        this.uuid = connection.getUUID();
        this.userDocument = connection.getUserDocumentRef();
        this.db = connection.getDB();
    }

    public DocumentReference getUserDocument(){
        this.userDocument = this.storeConnection.getUserDocumentRef();
        return this.userDocument;
    }
    /**
     * Author: Erin-Marie
     * adds the newUser the AllUsers collection
     * stored as a custom object, which requires the public empty constructor in the UserProfile class
     * @param newUser object
     * Reference: <a href="https://firebase.google.com/docs/firestore/manage-data/add-data?_gl=1">...</a>*189tp3e*_up*MQ..*_ga*MTI4NzA3MTQ3MC4xNzI5NzI3MjA0*_ga_CW55HF8NVT*MTcyOTcyNzIwNC4xLjAuMTcyOTcyNzIwNC4wLjAuMA..
     */
    public void addUser(UserProfile newUser){
        this.db.collection("AllUsers").document("User" + uuid).set(newUser);
        this.userDocument = this.storeConnection.getUserDocumentRef();
        Log.v("DatabaseRead", "Successfully read from the database: " + newUser.getUuid());
    }

    /**
     * Author: Erin-Marie
     * gets the given users data from their document, and returns it as a UserProfile object
     * stored as a custom object, so we have to convert the fetched data back into a UserProfile object
     * @return user UserProfile object that was requested
     * Reference: <a href="https://firebase.google.com/docs/firestore/manage-data/add-data?_gl=1">...</a>*189tp3e*_up*MQ..*_ga*MTI4NzA3MTQ3MC4xNzI5NzI3MjA0*_ga_CW55HF8NVT*MTcyOTcyNzIwNC4xLjAuMTcyOTcyNzIwNC4wLjAuMA..
     */
//    public UserProfile getCurrentUser(){
//        DocumentReference docRef = storeConnection.getUserDocumentRef();
//        DocumentSnapshot documentSnapshot = storeConnection.getDocumentSnapshot(docRef);
//        Log.v(TAG, "going to make userProfile object from documentSnapshot");
//       UserProfile user = documentSnapshot.toObject(UserProfile.class);
//        Log.v(TAG, "userProfile object made from documentSnapshot");
//        return user;
//    }
}
