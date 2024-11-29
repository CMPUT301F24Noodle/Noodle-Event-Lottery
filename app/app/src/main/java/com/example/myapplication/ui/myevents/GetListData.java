package com.example.myapplication.ui.myevents;

import android.util.Log;

import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;

public class GetListData {
    EventDB eventDB;
    ArrayList<UserProfile> entrants = new ArrayList<>();
    ArrayList<UserProfile> winners = new ArrayList<>();
    ArrayList<UserProfile> declined = new ArrayList<>();
    ArrayList<UserProfile> accepted = new ArrayList<>();

    public GetListData(EventDB eventDB) {
        this.eventDB = eventDB;
    }

    public HashMap<String, ArrayList<UserProfile>> getData(){
        HashMap<String, ArrayList<UserProfile>> expandableListDetail = new HashMap<String, ArrayList<UserProfile>>();
        //All Waitlisted (Entrants)
//        ArrayList<UserProfile> entrants = entrants.addAll(eventDB.getEntrantsList());
//        ArrayList<UserProfile> winners = eventDB.getWinnersList();
//        ArrayList<UserProfile> declined = eventDB.getDeclinedList();
//        ArrayList<UserProfile> accepted = eventDB.getAcceptedList();

        entrants.addAll(eventDB.getEntrantsList());
        winners.addAll(eventDB.getWinnersList());
        declined.addAll(eventDB.getDeclinedList());
        accepted.addAll(eventDB.getAcceptedList());
        Log.v("getData", "size of entrants list: " + entrants.size());

        expandableListDetail.put("Entrants", entrants);
        expandableListDetail.put("Winners", winners);
        expandableListDetail.put("Declined", declined);
        expandableListDetail.put("Accepted", accepted);


        return expandableListDetail;
    }
}
