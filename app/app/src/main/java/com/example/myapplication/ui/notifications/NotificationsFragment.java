package com.example.myapplication.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.myapplication.MainActivity;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.notificationClasses.Notification;
import com.example.myapplication.objects.userProfileClasses.UserProfile;


import java.util.ArrayList;


/**
 * Author: Erin-Marie
 * USERSTORIES: US.01.04.01, US.01.04.02
 * Uses the notifications_activity.XML layout file
 * TODO: make the class that will represent the notifications activity
 *       display the users array of notifications, notifications are not interacted with beyond scrolling
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

        notifDB = connection.getNotifDB();
        user = connection.getUser();
        eventDB = connection.getEventDB();
        userDB = connection.getUserDB();

    }

}