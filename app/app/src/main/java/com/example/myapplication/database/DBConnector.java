package com.example.myapplication.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * Author: Erin-Marie
 * DBConnector class connects to the Firestore Firebase Database where app data will be stored
 * Gets the current Users ID and connects to their collection
 */
public class DBConnector {

    private FirebaseFirestore db; //firebase instance
    private String uuid; //Store the users ID


    /**
     * Author: Erin-Marie
     * Getter method for the FirebaseFirestore instance, getter for the DataBase
     * @return database instance
     */
    public FirebaseFirestore getDB() {
        return this.db;
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
    protected String getUUID(Context context) {
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
     * getUserCollection(subCollection) retrieves a reference to the users subcollection of the DB
     * @param subCollection the tag for the subcollection of the current Users collection
     * @return returns a CollectionReference to the collection associated with the current UUID
     * TESTME
     */
    public CollectionReference getUserCollection(String subCollection) {
        CollectionReference doc = this.db.collection("AllUsers").document("User" + uuid).collection(subCollection);
        return doc;
    }
}
