package com.example.myapplication.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;

import java.util.UUID;

/**
 * Author: Erin-Marie
 * Class for a user object, stores any profile data they choose to add, and has method to get their UUID
 * TODO: connect UserProfile to db, to get the users db, and their collection
 */
public class UserProfile {
    String firstName;
    String lastName;
    String email;
    Image profilePicture;
    Integer permissions; //




    /**
     * Author: Erin-Marie
     * Method to get the users unique UUID
     * Citation: <a href="https://developer.android.com/training/data-storage/shared-preferences">...</a>
     * @param context which will provide the calling activity
     * @return strUUID, which is the users UUID in string form
     * TODO: write tests
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
            editor.commit();
        }
        return strUuid;
    }
}

