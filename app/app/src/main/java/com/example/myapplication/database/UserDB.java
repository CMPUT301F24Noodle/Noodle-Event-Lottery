package com.example.myapplication.database;

import android.util.Log;

import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

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
    public UserProfile currentUser;
    public String uuid;

    public UserDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.storeConnection = connection;
        this.allUsers = connection.getAllUsersCollection();
        this.uuid = connection.getUUID();
        this.userDocument = connection.getUserDocumentRef();
        this.db = connection.getDB();
    }

    public void updateUserDB(){
        //update the users DocumentReference value


    }

    public DocumentReference getUserDocument(){
        this.userDocument = this.storeConnection.getUserDocumentRef();
        return this.userDocument;
    }

    /**
     * Author: Erin-Marie
     * adds the newUser the AllUsers collection
     * stored as a custom object, which requires the public empty constructor in the UserProfile class
     * Reference: <a href="https://firebase.google.com/docs/firestore/manage-data/add-data?_gl=1">...</a>*189tp3e*_up*MQ..*_ga*MTI4NzA3MTQ3MC4xNzI5NzI3MjA0*_ga_CW55HF8NVT*MTcyOTcyNzIwNC4xLjAuMTcyOTcyNzIwNC4wLjAuMA..
     */
    public void addUser(){
        UserProfile newUser = new UserProfile(this.uuid);
        this.db.collection("AllUsers").document("User" + uuid).set(newUser);
        this.userDocument = getUserDocument();
        this.currentUser = newUser;
        Log.v("DatabaseRead", "Successfully added New user to db, User:  " + newUser.getUuid());
    }

    public void setProfile(DocumentSnapshot snapshot){
        UserProfile user = snapshot.toObject(UserProfile.class);
        this.currentUser = user;
        this.userDocument = getUserDocument();

    }

    //Returns a DocumentSnapshot object through an onComplete listener
    public void checkUserExists(OnSuccessListener<DocumentSnapshot> listener) {
        DocumentReference docRef = this.storeConnection.getUserDocumentRef();; // get the document reference for the current users document from the AllUsers Collection
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d(TAG, "user already exists:" + uuid);
                listener.onSuccess(snapshot);
            } else {
                Log.d(TAG, "user not already exists:" + uuid);
                listener.onSuccess(null);

            }
        });


    }

    public UserProfile getCurrentUser(){
        return this.currentUser;
    }
}

