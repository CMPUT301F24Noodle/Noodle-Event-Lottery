package com.example.myapplication.database;

import android.util.Log;

import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import org.w3c.dom.Document;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;
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
    private DocumentReference userDocument;
    private DBConnection storeConnection;
    private FirebaseFirestore db;

    public UserDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.userDocument = connection.getUserDocument();
    }

    /**
     * Author: Erin-Marie
     * adds the newUser the AllUsers collection
     * stored as a custom object, which requires the public empty constructor in the UserProfile class
     * @param newUser object
     * Reference: <a href="https://firebase.google.com/docs/firestore/manage-data/add-data?_gl=1">...</a>*189tp3e*_up*MQ..*_ga*MTI4NzA3MTQ3MC4xNzI5NzI3MjA0*_ga_CW55HF8NVT*MTcyOTcyNzIwNC4xLjAuMTcyOTcyNzIwNC4wLjAuMA..
     */
    public void addUser(UserProfile newUser){
        userDocument.set(newUser);
        Log.v("DatabaseRead", "Successfully read from the database: " + newUser.getUuid());
    }

    /**
     * Author: Erin-Marie
     * gets the given users data from their document
     * stored as a custom object, so we have to convert the fetched data back into a UserProfile object
     * @return user UserProfile object that was requested
     * Reference: <a href="https://firebase.google.com/docs/firestore/manage-data/add-data?_gl=1">...</a>*189tp3e*_up*MQ..*_ga*MTI4NzA3MTQ3MC4xNzI5NzI3MjA0*_ga_CW55HF8NVT*MTcyOTcyNzIwNC4xLjAuMTcyOTcyNzIwNC4wLjAuMA..
     */
    public void getUser(UUID uuid){
        DocumentReference docRef = db.collection("AllUsers").document("User"+uuid);

        //make sure the document exists
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //since we store the user document using a custom object in firestore, need to convert the data back to a UserProfile object
                UserProfile user = documentSnapshot.toObject(UserProfile.class);
                //TODO: need to catch user value with a listener in the calling method, but idk where that is
                Log.v("DatabaseRead", "Successfully read from the database: " + uuid);

            }
        });
    }
}
