package com.example.myapplication.database;

import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class MockMainActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setUpDB(){
        // Add the user to the db, added by Erin-Marie, if it breaks everything its my
        // fault
        this.connection = new MockDBConnection(); // connection to the base db
        this.userDB = connection.getUserDB(); // the current users collection reference
        this.eventDB = connection.getEventDB();
        //THESE will be removed
        this.notifDB = connection.getNotifDB();
        this.facilityDB = connection.getFacilityDB();

        this.uuid = connection.getUUID(); // store the current users uuid
        // check if the user is already in the db
        this.userDB.checkUserExists(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot != null) { // user is already in the database
                    // fetch their profile data, make it a userprofile object, and store it as
                    // currentUser
                    // done through addUser() method in order to get the value returned from
                    // checkUserExists()
                    userDB.setCurrentProfile(snapshot);
                    user = userDB.getCurrentUser();
                    eventDB.getUserEnteredEvents(user);
                    connection.setUser(user);
                    notifDB.getUserNotifications(); //this return value doesn't matter, this just needs to be called to intitiate their list of notifications
                    Log.v("SetUpDB", "Set profile for existing user");

                } else { // User is not already in the database
                    // create a new profile objet, and store it in the db
                    // done through addUser() method in order to get the value returned from
                    // checkUserExists()
                    userDB.addCurrentUser();
                    user = userDB.getCurrentUser();
                    eventDB.getUserEnteredEvents(user);
                    eventDB.getUserOrgEvents(user);
                    connection.setUser(user);
                    Log.v("SetUpDB", "Set profile for new user");
                }
            }
        });
        // sets the currentUser attribute for MainActivity
        //TESTME: omg this fucking worked it fetched the profile without creating a new one or overwriting it
    }
}
