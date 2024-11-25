package com.example.myapplication.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;


/**
 * Author: Erin-Marie
 * class that does all database operations for UserProfile objects
 */
public class UserDB {

    //For logcat
    private final static String TAG = "UserDB";

    //reference to the current users document in the AllUsers collection
    public DocumentReference userDocumentReference;
    public DBConnection storeConnection;
    public CollectionReference allUsers;
    public FirebaseFirestore db;
    public UserProfile currentUser;
    public String uuid;


    /**
     * Class constructor
     * @param connection DBConnection from mainActivity
     */
    public UserDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.storeConnection = connection;
        this.allUsers = connection.getAllUsersCollection();
        this.uuid = connection.getUUID();
        this.userDocumentReference = connection.getUserDocumentRef();
        this.db = connection.getDB();

    }

    /**
     * Author: Erin-Marie
     * adds a newUser to the AllUsers collection, using the current users uuid as the document id
     * stored as a custom object, which requires the public empty constructor in the UserProfile class
     * Sets the currentUser and userDocument Reference attributes of the UserDB instance
     * Reference: <a href="https://firebase.google.com/docs/firestore/manage-data/add-data?_gl=1">...</a>*189tp3e*_up*MQ..*_ga*MTI4NzA3MTQ3MC4xNzI5NzI3MjA0*_ga_CW55HF8NVT*MTcyOTcyNzIwNC4xLjAuMTcyOTcyNzIwNC4wLjAuMA..
     */
    public void addCurrentUser(){
        UserProfile newUser = new UserProfile(this.uuid);
        this.db.collection("AllUsers").document("User" + uuid).set(newUser);
        this.userDocumentReference = this.db.collection("AllUsers").document("User" + uuid);
        newUser.setDocRef(this.userDocumentReference);
        updateUserDocument(newUser);
        this.currentUser = newUser;
        Log.v("DatabaseRead", "Successfully added New user to db, User:  " + newUser.getUuid());
    }

    /**
     * Author: Erin-Marie
     * converts a DocumentSnapshot to a UserProfile object
     * Sets the currentUser and userDocument Reference attributes of the UserDB instance
     * use of toObject() requires the public empty constructor in the UserProfile class
     * @param snapshot of the users document in the AllUsers collection
     * Reference <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/DocumentSnapshot">...</a>
     */
    public void setCurrentProfile(DocumentSnapshot snapshot){
        this.currentUser = snapshot.toObject(UserProfile.class);
        this.userDocumentReference = getUserDocumentReference();

    }


    /**
     * Author: Erin-Marie
     * Looks up the users uuid in the AllUsers collection, and checks if they already exist in the database
     * @param listener onSuccess listener called from MainActivity, returns null if the user does not already exist
     * Making this all work has killed me
     * Reference: <a href="https://developer.android.com/reference/com/google/android/play/core/tasks/OnSuccessListener">...</a>
     */
    public void checkUserExists(OnSuccessListener<DocumentSnapshot> listener) {
        DocumentReference docRef = this.storeConnection.getUserDocumentRef();; // get the document reference for the current users document from the AllUsers Collection
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.v(TAG, "user already exists:" + uuid);
                listener.onSuccess(snapshot);
            } else {
                Log.v(TAG, "user not already exists:" + uuid);
                listener.onSuccess(null);
            }
        });
    }

    /**
     * Author: Erin-Marie
     * Method to be called when a userprofile needs to be removed from the db
     * Deletes the user's document from firebase
     * TODO: find out what happens if a user has entered an event, then their profile is deleted.
     *      do all the methods that get lists of entrants still work?
     *      do all the methods for sending notifications still work?
     *      make sure none of the methods throw an error if they cannot find a user document in the db
     * @param user the user profile to be deleted
     */
    public void deleteUser(UserProfile user){
        this.db.collection("AllUsers").document("User" + user.getUuid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting user: User" + user.getUuid(), e);
                    }
                });

    }

    /**
     * Author: Erin-Marie
     * Call this after editing a user profile to have the db reflect the updates
     * @param user to be updated
     * IDK if this will actually work but in theory it does
     */
    public void updateUserDocument(UserProfile user){
        this.userDocumentReference.set(user);
    }


    //US.02.03.01
    //IDk why this is here but im scared to delete it
    public void enterEvent(Event event){
        updateUserDocument(currentUser);

    }

    //getters and setters

    /**
     * Author: Erin-Marie
     * Getter for currentUser
     * @return Returns the UserProfile instance of the current user
     */
    public UserProfile getCurrentUser(){
        return this.currentUser;
    }

    /**
     * Author: Erin-Marie
     * Getter for the DocumentReference of the current users document in AllUsers collection
     * @return DocumentReference of the current users document in AllUsers collection
     */
    public DocumentReference getUserDocumentReference(){
        this.userDocumentReference = this.storeConnection.getUserDocumentRef();
        return this.userDocumentReference;
    }

    public DBConnection getStoreConnection() {
        return storeConnection;
    }

    public void setStoreConnection(DBConnection storeConnection) {
        this.storeConnection = storeConnection;
    }

    public void setUserDocumentReference(DocumentReference userDocumentReference) {
        this.userDocumentReference = userDocumentReference;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public CollectionReference getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(CollectionReference allUsers) {
        this.allUsers = allUsers;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


}

