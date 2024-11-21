package com.example.myapplication.ui.notifications;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.notificationClasses.Notification;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.DocumentReference;


import java.util.ArrayList;


/**
 * Author: Erin-Marie
 * USERSTORIES: US.01.04.01, US.01.04.02
 * Uses the notifications_activity.XML layout file
 * QUESTION: what notifications need to be device notifications each time the app is launched?
 *  is there a way to mark which ones have already been seen?
 *  should notifications populate while navigating the app as well?
 */
public class NotificationsFragment extends Fragment {


    private FragmentNotificationsBinding binding;

    public MainActivity main;
    public DBConnection connection;
    public UserDB userDB; // userDB instance for the current user
    public EventDB eventDB;
    public NotificationDB notifDB;
    public String uuid;
    public UserProfile user;
    public Event event;
    public NotificationManager notifMan;
    public FacilityDB facilityDB;

    public ArrayList<Notification> notificationsList;
    public NotificationArrayAdapter adapter;

    /**
     * Author: Erin-Marie
     * On create view for the notification fragment
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return view to be displayed
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //get all the db connection from MainActivtiy
        getVarFromMain();


        //populate the users notification list
        //notifDB.myNotifs = new ArrayList<Notification>();


        notificationsList = notifDB.getUserNotifications();


        //set up the view binding
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


//        //TODO Remove; this is just for testing that the notification spawns
//        Button button = root.findViewById(R.id.test_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ArrayList<DocumentReference> recipients = new ArrayList<DocumentReference>();
//                recipients.add(user.getDocRef());
//
//                Notification notif = new Notification("test notification", "test message", recipients, user);
//
//                Log.v("notiffrag", "notif being called by set up db");
//                main.displayNotification(notif);
//            }
//        });

        ListView listView = binding.notificationsList;

        adapter = new NotificationArrayAdapter(requireContext(), notificationsList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /**
     * Author: Erin-Marie
     * Gets some of the variables from MainActivity that we will need
     */
    public void getVarFromMain() {
        main = (MainActivity) getActivity();
        assert main != null;
        connection = main.connection;
        notifMan = main.notificationManager;
        notifDB = connection.getNotifDB();
        user = connection.getUser();
        eventDB = connection.getEventDB();
        userDB = connection.getUserDB();

    }

}