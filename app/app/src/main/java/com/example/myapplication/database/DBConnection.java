package com.example.myapplication.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.Reference;
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
     * TESTME
     */
    public DocumentReference getUserDocumentRef() {
        //CollectionReference doc = this.db.collection("AllUsers").document("User" + uuid).collection(subCollection);
        DocumentReference docRef = this.db.collection("AllUsers").document("User" + uuid);
        return docRef;
    }

    /**
     * Author: Erin-Marie
     * returns a snapshot (the contents) of a single document that is passed to it
     * @param docRef a DocumentReference to a document that does or does not exist in the DB
     * @return returns a DocumentSnapshot of the document
     * TESTME
     */
    public UserProfile getDocumentSnapshot(DocumentReference docRef) {
        //final DocumentSnapshot[] snapshot = new DocumentSnapshot[1];
        final UserProfile[] user = new UserProfile[1];
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user[0] = task.getResult().toObject(UserProfile.class);

//                if (snapshot[0].exists()) {
//                    Log.d(TAG, "DocumentSnapshot data: " + snapshot[0].getData());
//                } else {
//                    Log.d(TAG, "No such document");
//                }
//            } else {
//                Log.d(TAG, "get failed with ", task.getException());
//            }
        }});

        return user[0];
    }

    /**
     * Checks if the current user is in the db or not
     * Takes no parameters, will call getUserDocRef and getDocumentSnapshot within method
     * Checks if the snapshot returned by GetDocumentSnapshot exists or not
     * @return doesExist Boolean, if TRUE the user already exists in the db
     */
//    public Boolean checkUserDocExists(){
//        DocumentReference docRef = getUserDocumentRef();
//        DocumentSnapshot snapshot = getDocumentSnapshot(docRef);
//        if (snapshot.exists() == Boolean.TRUE) {
//            Log.d(TAG, "DocumentSnapshot data: " + snapshot.getData());
//            return Boolean.TRUE;
//        } else if (snapshot.exists() == Boolean.FALSE) {
//            Log.d(TAG, "No such document");
//            return Boolean.FALSE;
//        } else {
//            Log.d(TAG, "checkUserDocExists is still broken");
//        }
//
//        return Boolean.FALSE;
//    }

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

    public CollectionReference getAllUsersCollection(){
        return this.db.collection("AllUsers");
    }



    //TODO: make a method to get the allusers collection
    //reference to AllUsers collection
    //private CollectionReference allUsersCollection;
}
