package com.example.myapplication.ui.notifications;

import android.graphics.Insets;
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
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.DocumentReference;
import com.google.zxing.WriterException;


import org.w3c.dom.Document;

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
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return view to be displayed
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //get all the db stuff from mainActivity
        getVarFromMain();

        //populate the users notification list
        //notifDB.myNotifs = new ArrayList<Notification>();
        notificationsList = notifDB.getUserNotifications();

        //set up the view binding
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button button = root.findViewById(R.id.testingbuttonforerin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    testing();
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }

                // now show the fragment
            }
        });
        Button button2 = root.findViewById(R.id.testingbuttonforerin2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testing2();
                // now show the fragment
            }
        });

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
    public void getVarFromMain(){
        main = (MainActivity) getActivity();
        assert main != null;
        connection = main.connection;
        user = main.user;
        assert user != null;
        userDB = main.userDB;
        assert userDB != null;
        eventDB = main.eventDB;
        uuid = main.uuid;
        notifDB = main.notifDB;
        facilityDB = main.facilityDB;
    }

    public void testing() throws WriterException {

        Facility facility = user.getFacility();
        Event event = new Event(facility, user, "Party1", null, null, -1, null, Boolean.FALSE);
        Event event2 = new Event(facility, user, "Party2", null, null, -1, null, Boolean.FALSE);
        Event event3 = new Event(facility, user, "Party3", null, null, -1, null, Boolean.FALSE);
        //eventDB.addEvent(event);
        eventDB.addEvent(event2);
        //eventDB.addEvent(event3);

        this.event = event2;



    }

    //BROKEN the event exists and gets updated with the user as a entrant, but the event docref is null in the users document on db
    public void testing2(){
        //BROKEN uncomment this and itll crash eventDB.updateEvent(this.event);
        userDB.enterEvent(this.event);
        eventDB.updateEvent(this.event);


    }
}
