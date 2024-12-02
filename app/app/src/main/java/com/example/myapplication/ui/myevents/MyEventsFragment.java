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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMyeventsBinding;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.ui.myevents.viewMyEvents.OrganizedEventArrayAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


/**
 * Author: Nishchay, Erin-Marie
 * Class responsible for the My Organized Events page and its interaction
 * Also responsible for opening child fragments
 */

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

                openEditEventFragment(eventList.get(i), view);
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
                //openAddEventsFragment(v);
                Navigation.findNavController(v).navigate(R.id.nav_add_events);
                eventAdapter.notifyDataSetChanged();

            }

        });


        return root;
    }

    /**
     * Author: Erin-Marie
     * method to initiate the query to get the users organized events from the db
     */
    public void getMyEvents(){
        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        assert main != null;

        this.eventDB = main.eventDB;
        this.currentUserProfile = main.user;
        eventDB.getUserOrgEvents(currentUserProfile);
    }

    /**
     * Author: Nishchay
     * Edited: Erin-Marie
     * Method called when the user selects an event from their Oranized event list view
     * Constructs a bundle of arguments to send to the new fragment
     * @param event the event selected from the listview
     */
    private void openEditEventFragment(Event event, View v){
        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            main.currentEvent = event; // pass this in so fragment can access it
        }

        //Navigation.findNavController(v).navigate(R.id.nav_edit_event, args);
        Navigation.findNavController(v).navigate(R.id.nav_edit_event);

        eventAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}