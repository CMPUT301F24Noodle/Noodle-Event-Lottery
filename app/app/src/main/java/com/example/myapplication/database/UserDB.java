package com.example.myapplication.database;

import android.util.Log;

import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public DBConnection getStoreConnection() {
        return storeConnection;
    }

    public void setStoreConnection(DBConnection storeConnection) {
        this.storeConnection = storeConnection;
    }

    public void setUserDocument(DocumentReference userDocument) {
        this.userDocument = userDocument;
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
    public DocumentReference getUserDocument(){
        this.userDocument = this.storeConnection.getUserDocumentRef();
        return this.userDocument;
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
        this.userDocument = getUserDocument();
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
        UserProfile user = snapshot.toObject(UserProfile.class);
        this.currentUser = user;
        this.userDocument = getUserDocument();

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
                Log.d(TAG, "user already exists:" + uuid);
                listener.onSuccess(snapshot);
            } else {
                Log.d(TAG, "user not already exists:" + uuid);
                listener.onSuccess(null);
            }
        });


    }
    
}

