package com.example.myapplication.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * AdminEventsFragment
 *
 * This fragment allows the admin to view a list of events and delete them if necessary.
 * It fetches events from the Firestore database and displays them in a ListView.
 * Each event can be deleted using a dialog confirmation.
 *
 * Author: Nishchay Ranjan
 */
public class AdminEventsFragment extends Fragment {

    private static final String TAG = "AdminEventsFragment"; // Tag for logging

    private ListView eventListView; // ListView for displaying events
    private ArrayList<String> eventList; // List of event details as strings
    private ArrayList<String> eventIDs; // List of event IDs
    private ArrayList<Event> fullEventList; // List of Event objects
    private ArrayAdapter<Event> eventAdapter; // Adapter for ListView
    private FirebaseFirestore db; // Firestore database instance

    /**
     * Default constructor for AdminEventsFragment.
     * Required for instantiation by the Android framework.
     */
    public AdminEventsFragment() {
        // Required empty public constructor
    }

    /**
     * onCreateView
     *
     * Initializes the fragment's view, sets up the ListView adapter, and begins fetching events
     * from the database.
     *
     * @param inflater LayoutInflater for inflating the fragment's view
     * @param container Optional parent view
     * @param savedInstanceState Bundle containing saved state
     * @return View representing the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_admin_listview, container, false);

        // Initialize UI components and variables
        eventListView = rootView.findViewById(R.id.admin_item_list);
        eventList = new ArrayList<>();
        eventIDs = new ArrayList<>();
        fullEventList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Set up the adapter for the ListView
        eventAdapter = new AdminEventArrayAdapter(requireContext(), fullEventList);
        eventListView.setAdapter(eventAdapter);

        // Set click listener for ListView items
        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            String eventData = eventList.get(position);
            showEventDetailsDialog(eventData, position);
        });

        // Fetch all events from the database
        fetchAllEvents();
        return rootView;
    }

    /**
     * fetchAllEvents
     *
     * Fetches all events from the Firestore database and populates the ListView.
     * Retrieves event details, formats the data, and updates the adapter.
     */
    private void fetchAllEvents() {
        db.collection("AllEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        eventList.clear();
                        eventIDs.clear();
                        fullEventList.clear();

                        // Process each document in the query result
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            fullEventList.add(document.toObject(Event.class));
                            Timestamp eventDateTimestamp = document.getTimestamp("eventDate");
                            String eventDate = null;

                            if (eventDateTimestamp != null) {
                                eventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .format(eventDateTimestamp.toDate());
                            }

                            String eventDetails = document.getString("eventDetails");
                            String eventName = document.getString("eventName");
                            String eventData = "Name: " + eventName + "\nDate: " + eventDate + "\nDetails: " + eventDetails;

                            eventList.add(eventData);
                            eventIDs.add(document.getId());

                            Log.d(TAG, "Event Data: " + eventData);
                        }

                        // Notify the adapter of dataset changes
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching events: ", task.getException());
                    }
                });
    }

    /**
     * showEventDetailsDialog
     *
     * Displays a dialog to confirm deletion of the selected event.
     *
     * @param eventData String containing event details
     * @param position  Position of the event in the ListView
     */
    private void showEventDetailsDialog(String eventData, int position) {
        String eventID = eventIDs.get(position); // Get event ID based on position
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you would like to delete this event?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    Toast.makeText(requireContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                    deleteEvent(eventID);
                    Event delEvent = fullEventList.get(position);
                    fullEventList.remove(position);
                    eventList.remove(position);
                    eventAdapter.notifyDataSetChanged();
                    if (delEvent != null) {
                        db.collection("AllEvents").document(delEvent.getEventID()).delete();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss())
                .show();
    }

    /**
     * deleteEvent
     *
     * Deletes an event from the Firestore database by its ID.
     *
     * @param eventID The ID of the event to delete
     */
    private void deleteEvent(String eventID) {
        db.collection("AllEvents").document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully deleted: " + eventID))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event: " + eventID, e));
    }
}
