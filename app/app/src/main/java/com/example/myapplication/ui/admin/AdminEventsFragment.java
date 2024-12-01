/**
 * AdminEventsFragment.java
 *
 * This class represents a fragment that displays and manages administrative events.
 * It allows viewing, editing, and deleting events stored in a Firestore database.
 * The events are presented in a list, and users can interact with individual events
 * to perform CRUD operations.
 *
 * Author: Nishchay Ranjan
 */

package com.example.myapplication.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminEventsFragment extends Fragment {

    private static final String TAG = "AdminEventsFragment";

    // UI components
    private ListView eventListView;
    private ArrayList<String> eventList;
    private ArrayList<String> eventIDs;
    private ArrayAdapter<String> eventAdapter;

    // Firebase Firestore instance
    private FirebaseFirestore db;

    /**
     * Default constructor required for instantiation.
     */
    public AdminEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates and returns the view hierarchy associated with this fragment.
     *
     * @param inflater LayoutInflater object to inflate views.
     * @param container Parent view that this fragment's UI will be attached to.
     * @param savedInstanceState Saved state for fragment recreation, if any.
     * @return The root view for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_admin_event, container, false);

        // Initialize UI components
        eventListView = rootView.findViewById(R.id.event_list_view);
        eventList = new ArrayList<>();
        eventIDs = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Set up list adapter
        eventAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, eventList);
        eventListView.setAdapter(eventAdapter);

        // Set event list item click listener
        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            String eventData = eventList.get(position);
            String eventID = eventIDs.get(position);
            showEventDetailsDialog(eventData, eventID);
        });

        // Fetch all events from the database
        fetchAllEvents();

        return rootView;
    }

    /**
     * Fetches all events from the Firestore database and populates the list view.
     */
    private void fetchAllEvents() {
        db.collection("AllEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        eventList.clear();
                        eventIDs.clear();

                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
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

                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching events: ", task.getException());
                    }
                });
    }

    /**
     * Displays a dialog for editing or deleting an event.
     *
     * @param eventData Details of the selected event.
     * @param eventID Firestore document ID of the selected event.
     */
    private void showEventDetailsDialog(String eventData, String eventID) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.admin_text_delete_view_event, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        // Dialog UI components
        EditText editEventTitle = dialogView.findViewById(R.id.edit_event_title);
        EditText editEventDate = dialogView.findViewById(R.id.edit_event_date);
        EditText editEventDetails = dialogView.findViewById(R.id.edit_event_details);
        Button saveButton = dialogView.findViewById(R.id.save_button);
        Button deleteButton = dialogView.findViewById(R.id.delete_button);
        Button backButton = dialogView.findViewById(R.id.back_button);

        // Populate dialog fields with current event data
        String[] eventParts = eventData.split("\n");
        String eventName = eventParts[0].replace("Name: ", "");
        String eventDateStr = eventParts[1].replace("Date: ", "");
        String eventDetails = eventParts[2].replace("Details: ", "");

        editEventTitle.setText(eventName);
        editEventDate.setText(eventDateStr);
        editEventDetails.setText(eventDetails);

        // Save button functionality
        saveButton.setOnClickListener(v -> {
            String updatedEventName = editEventTitle.getText().toString().trim();
            String updatedEventDate = editEventDate.getText().toString().trim();
            String updatedEventDetails = editEventDetails.getText().toString().trim();

            if (updatedEventName.isEmpty() || updatedEventDate.isEmpty() || updatedEventDetails.isEmpty()) {
                Toast.makeText(requireContext(), "All fields must be filled out!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Timestamp updatedTimestamp = new Timestamp(
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(updatedEventDate)
                );

                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("eventName", updatedEventName);
                updatedData.put("eventDate", updatedTimestamp);
                updatedData.put("eventDetails", updatedEventDetails);

                db.collection("AllEvents").document(eventID)
                        .update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
                            fetchAllEvents();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error updating event: ", e);
                            Toast.makeText(requireContext(), "Failed to update event.", Toast.LENGTH_SHORT).show();
                        });
            } catch (Exception e) {
                Log.e(TAG, "Invalid date format", e);
                Toast.makeText(requireContext(), "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button functionality
        deleteButton.setOnClickListener(v -> {
            deleteEvent(eventID);
            int position = eventIDs.indexOf(eventID);
            if (position != -1) {
                eventList.remove(position);
                eventIDs.remove(position);
                eventAdapter.notifyDataSetChanged();
            }

            Toast.makeText(requireContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Back button functionality
        backButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Deletes the selected event from the Firestore database.
     *
     * @param eventID Firestore document ID of the event to delete.
     */
    private void deleteEvent(String eventID) {
        db.collection("AllEvents").document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully deleted: " + eventID))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event: " + eventID, e));
    }
}
