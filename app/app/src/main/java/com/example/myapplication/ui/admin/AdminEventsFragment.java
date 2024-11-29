package com.example.myapplication.ui.admin;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminEventsFragment extends Fragment {

    private static final String TAG = "AdminEventsFragment";

    private ListView eventListView;
    private ArrayList<String> eventList;
    private ArrayList<String> eventIDs; // To store event IDs for deletion
    private ArrayAdapter<String> eventAdapter;
    private FirebaseFirestore db;

    public AdminEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.item_admin_event, container, false);

        // Initialize ListView, ArrayList, and Firestore
        eventListView = rootView.findViewById(R.id.event_list_view);
        eventList = new ArrayList<>();
        eventIDs = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Set up the adapter
        eventAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, eventList);
        eventListView.setAdapter(eventAdapter);

        // Set up long-press listener for deletion
        eventListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Get the event ID associated with the clicked item
            String eventID = eventIDs.get(position);

            // Delete the event from Firestore
            deleteEvent(eventID);

            // Remove the item from the list and update the adapter
            eventList.remove(position);
            eventIDs.remove(position);
            eventAdapter.notifyDataSetChanged();

            // Show a toast
            Toast.makeText(requireContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();

            return true; // Indicates the long-press was handled
        });

        // Fetch and populate events
        fetchAllEvents();

        return rootView;
    }

    private void fetchAllEvents() {
        // Reference the "AllEvents" collection
        db.collection("AllEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Clear existing data to avoid duplicates
                        eventList.clear();
                        eventIDs.clear();

                        // Loop through each document in the collection
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Fetch the eventDate field as a Timestamp
                            Timestamp eventDateTimestamp = document.getTimestamp("eventDate");
                            String eventDate = null;

                            if (eventDateTimestamp != null) {
                                // Format the Timestamp as a String (e.g., "2024-11-29")
                                eventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .format(eventDateTimestamp.toDate());
                            }

                            // Fetch the eventDetails and eventName fields as Strings
                            String eventDetails = document.getString("eventDetails");
                            String eventName = document.getString("eventName");

                            // Combine the data into a single string for display
                            String eventData = "Name: " + eventName + "\nDate: " + eventDate + "\nDetails: " + eventDetails;

                            // Add the formatted string to the list
                            eventList.add(eventData);

                            // Store the event ID for later deletion
                            eventIDs.add(document.getId());

                            Log.d(TAG, "Event Data: " + eventData);
                        }

                        // Notify adapter about data changes
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        // Log an error if the query fails
                        Log.e(TAG, "Error fetching events: ", task.getException());
                    }
                });
    }

    private void deleteEvent(String eventID) {
        // Delete the event from Firestore
        db.collection("AllEvents").document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully deleted: " + eventID))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event: " + eventID, e));
    }
}
