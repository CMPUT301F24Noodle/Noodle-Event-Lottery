package com.example.myapplication.ui.myevents;

import android.util.Log;

import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;

public class GetListData {
    EventDB eventDB;

    public GetListData(EventDB eventDB) {
        this.eventDB = eventDB;
    }

    public HashMap<String, ArrayList<UserProfile>> getData(){
        HashMap<String, ArrayList<UserProfile>> expandableListDetail = new HashMap<String, ArrayList<UserProfile>>();
        //All Waitlisted (Entrants)
        ArrayList<UserProfile> entrants = eventDB.getEntrantsList();
        ArrayList<UserProfile> winners = eventDB.getWinnersList();
        ArrayList<UserProfile> declined = eventDB.getDeclinedList();
        ArrayList<UserProfile> accepted = eventDB.getAcceptedList();
        Log.v("getData", "size of entrants list: " + entrants.size());

        expandableListDetail.put("Entrants", entrants);
        expandableListDetail.put("Winners", winners);
        expandableListDetail.put("Declined", declined);
        expandableListDetail.put("Accepted", accepted);


        return expandableListDetail;
    }
}
