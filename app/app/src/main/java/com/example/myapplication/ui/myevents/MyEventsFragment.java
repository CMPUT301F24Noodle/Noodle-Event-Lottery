/**
 * Author: Nishchay Ranjan
 *
 * This fragment displays a list of events from the Firestore collection "AllEvents" and provides an option to add new events.
 * - Retrieves event names from Firestore and displays them in a ListView.
 * - Allows navigation to an AddEventsFragment for adding new events.
 *
 * Important Components:
 * - onCreateView: Initializes UI elements, sets up Firestore, and fetches events.
 * - fetchAllEvents: Fetches and displays all events from Firestore.
 * - openAddEventsFragment: Navigates to the AddEventsFragment to add a new event.
 */

package com.example.myapplication.ui.myevents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMyeventsBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.ui.notifications.NotificationArrayAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MyEventsFragment extends Fragment {

    private FragmentMyeventsBinding binding;
    private ArrayList<Event> eventList;


    private OrganizedEventArrayAdapter eventAdapter;

    private EventDB eventDB;
    private UserProfile currentUserProfile;
    private FirebaseFirestore db; // Reference to Firebase Firestore

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getMyEvents();

        binding = FragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        //View view = inflater.inflate(R.layout.fragment_myevents, container, false);


        ListView listView = binding.createdEventList;
        FloatingActionButton createEventButton = binding.createEventButton;


        // Initialize the ListView with a simple adapter
        eventList = eventDB.getMyOrgEvents();
        Log.v("events", "size: " + eventList.size());

        eventAdapter = new OrganizedEventArrayAdapter(requireContext(), eventList);
        listView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Bundle manageArgs = new Bundle();
//                manageArgs.putSerializable("event", eventList.get(i));
//                manageArgs.putSerializable("eventDB", eventDB);
//                openManageEventFragment(manageArgs);

                openEditEventFragment(eventList.get(i));
            }
        });




        // Set up the FloatingActionButton click listener
        // Set OnClickListener for the FAB
        binding.createEventButton.setOnClickListener(v -> {
            if (currentUserProfile.getPrivileges() != 1){
                CharSequence text = "You need organizer privileges to create an event. Please add a facility to your profile.";
                Toast.makeText(this.getContext(), text, Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to create event fragment
                openAddEventsFragment();
            }

        });


        return root;
    }

    public void getMyEvents(){
        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        assert main != null;

        this.eventDB = main.eventDB;
        this.currentUserProfile = main.user;
        eventDB.getUserOrgEvents(currentUserProfile);
    }


    /**
     * Opens the AddEventsFragment, allowing the user to add a new event.
     */
    private void openManageEventFragment(Bundle manageArgs) {
        // Create a new instance of AddEventsFragment
        ManageEventFragment addManageEventFragment = new ManageEventFragment();
        addManageEventFragment.setArguments(manageArgs);

        // Begin the Fragment transaction
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, addManageEventFragment); // Ensure this ID matches your main container ID
        transaction.addToBackStack(null); // Adds the transaction to the back stack
        transaction.commit();
    }

    private void openEditEventFragment(Event event){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getEventDate());

        // Create arguments to pass to EditEventFragment
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        //args.putString("event_id", newEvent.getEventID());
        args.putString("event_name", event.getEventName());
//        args.putString("event_location",  location);
        args.putString("event_date_time", formattedDate);
        args.putString("event_details", event.getEventDetails());
        if (event.getEventOver() == Boolean.FALSE){
            args.putString("event_status", "Event Lottery Open");
        } else {
            args.putString("event_status", "Event Lottery Closed");
        }

        if (event.getMaxEntrants() == -1){
            args.putString("event_waiting_list", event.getWaitingListSize() + " entrants");
        } else {
            args.putString("event_waiting_list", event.getWaitingListSize() + " / " + event.getMaxEntrants());
        }


        EditEventFragment editEventFragment = new EditEventFragment();
        editEventFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, editEventFragment)
                .addToBackStack(null)
                .commit();

    }

    /**
     * Opens the AddEventsFragment, allowing the user to add a new event.
     */
    private void openAddEventsFragment() {
        // Create a new instance of AddEventsFragment
        AddEventsFragment addEventsFragment = new AddEventsFragment();
        Bundle args = new Bundle();
        args.putSerializable("eventDB", eventDB);
        addEventsFragment.setArguments(args);

        // Begin the Fragment transaction
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, addEventsFragment); // Ensure this ID matches your main container ID
        transaction.addToBackStack(null); // Adds the transaction to the back stack
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}