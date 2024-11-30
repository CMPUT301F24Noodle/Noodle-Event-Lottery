package com.example.myapplication.ui.registeredevents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.databinding.FragmentRegisteredEventsBinding;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Author: Sam Lee
 * Fragment for displaying a list of registered events.
 * This fragment contains a ListView that displays events the user is registered
 * for.
 * 
 */
public class RegisteredEventFragment extends Fragment {

    private FragmentRegisteredEventsBinding binding;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventList;
    private RegisteredEventArrayAdapter adapter;

    public MainActivity main;
    public DBConnection connection;
    public UserDB userDB; // userDB instance for the current user
    public EventDB eventDB;
    public NotificationDB notifDB;
    public String uuid;
    public UserProfile user;
    public Event event;
    public FacilityDB facilityDB;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        RegisteredEventViewModel registeredEventViewModel = new ViewModelProvider(this)
                .get(RegisteredEventViewModel.class);

        getVarFromMain();

        // Erin-Marie: Initialize the event list and adapter from db
        // eventList = eventDB.getMyEvents();
        getEvents();
        eventList = eventDB.getMyEvents();

        binding = FragmentRegisteredEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set OnClickListener for the FAB
        binding.qrButton.setOnClickListener(v -> {
            // Navigate to QRFragment
            Navigation.findNavController(v).navigate(R.id.nav_qr_fragment);
        });

        // Initialize the listView
        ListView listView = binding.registeredEventList;

        adapter = new RegisteredEventArrayAdapter(requireContext(), eventList, userDB, eventDB);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return root;
    }

    public RegisteredEventArrayAdapter getRegisteredEventArrayAdapter() {
        return adapter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Author Erin-Marie
     * get the users events by calling the eventDB method that will query all events
     * the user is an entrant in
     */
    public void getEvents() {
        eventDB.getUserEnteredEvents(user);

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