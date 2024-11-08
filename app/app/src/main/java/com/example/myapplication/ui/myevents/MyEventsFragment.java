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
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;

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
        binding = FragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firestore
        //db = FirebaseFirestore.getInstance();

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        assert main != null;

        this.eventDB = main.eventDB;
        this.currentUserProfile = main.user;

        // Initialize the ListView with a simple adapter
        eventList = new ArrayList<>();
        eventList = eventDB.getUserOrgEvents(currentUserProfile);


        ListView listView = binding.createdEventList;

        eventAdapter = new OrganizedEventArrayAdapter(requireContext(), eventList);
        listView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();


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

    /**
     * Fetches all events from the "AllEvents" Firestore collection and adds their names to the ListView.
     */
//    private void fetchAllEvents() {
//        db.collection("AllEvents")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                            for (DocumentSnapshot document : querySnapshot) {
//                                Event event = document.toObject(Event.class);
//                                if (event != null && event.getEventName() != null) {
//                                    eventNamesList.add(event.getEventName()); // Add event name to the list
//                                    Log.v("MyEventsFragment", "Event Name: " + event.getEventName()); // Log the event name
//                                }
//                            }
//                            adapter.notifyDataSetChanged(); // Update the ListView after all events are added
//                        } else {
//                            Log.d("MyEventsFragment", "No events found in AllEvents collection.");
//                        }
//                    } else {
//                        Log.e("MyEventsFragment", "Error fetching events: ", task.getException());
//                        Toast.makeText(getContext(), "Error fetching events.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

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