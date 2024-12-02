package com.example.myapplication.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.myapplication.objects.Event;
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

    private ListView eventListView;
    private ArrayList<String> eventList;
    private ArrayList<String> eventIDs;
    private ArrayList<Event> fullEventList;
    private ArrayAdapter<String> eventAdapter;
    private FirebaseFirestore db;

    public AdminEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_admin_event, container, false);

        eventListView = rootView.findViewById(R.id.event_list_view);
        eventList = new ArrayList<>();
        eventIDs = new ArrayList<>();
        fullEventList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        eventAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, eventList);
        eventListView.setAdapter(eventAdapter);

        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            String eventData = eventList.get(position);
            showEventDetailsDialog(eventData, position);
        });

        fetchAllEvents();
        return rootView;
    }

    private void fetchAllEvents() {
        db.collection("AllEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        eventList.clear();
                        eventIDs.clear();

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

                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching events: ", task.getException());
                    }
                });
    }

    private void showEventDetailsDialog(String eventData, int position) {
        String eventID = eventData;
        new AlertDialog.Builder(requireContext()) //
                .setTitle("Delete Event")
                .setMessage("Are you sure you would like to delete this event?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(requireContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                        deleteEvent(eventID);
                        Event delEvent = fullEventList.get(position);
                        eventList.remove(position);
                        assert delEvent != null;
                        db.collection("AllEvents").document(delEvent.getEventID()).delete();
                        eventAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
//        LayoutInflater inflater = LayoutInflater.from(requireContext());
//        View dialogView = inflater.inflate(R.layout.admin_text_delete_view_event, null);
//
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
//        builder.setView(dialogView);
//        android.app.AlertDialog dialog = builder.create();
//
//        EditText editEventTitle = dialogView.findViewById(R.id.edit_event_title);
//        EditText editEventDate = dialogView.findViewById(R.id.edit_event_date);
//        EditText editEventDetails = dialogView.findViewById(R.id.edit_event_details);
//        Button saveButton = dialogView.findViewById(R.id.save_button);
//        Button deleteButton = dialogView.findViewById(R.id.delete_button);
//        Button backButton = dialogView.findViewById(R.id.back_button);
//
//        String[] eventParts = eventData.split("\n");
//        String eventName = eventParts[0].replace("Name: ", "");
//        String eventDateStr = eventParts[1].replace("Date: ", "");
//        String eventDetails = eventParts[2].replace("Details: ", "");
//
//        editEventTitle.setText(eventName);
//        editEventDate.setText(eventDateStr);
//        editEventDetails.setText(eventDetails);
//
//        saveButton.setOnClickListener(v -> {
//            String updatedEventName = editEventTitle.getText().toString().trim();
//            String updatedEventDate = editEventDate.getText().toString().trim();
//            String updatedEventDetails = editEventDetails.getText().toString().trim();
//
//            if (updatedEventName.isEmpty() || updatedEventDate.isEmpty() || updatedEventDetails.isEmpty()) {
//                Toast.makeText(requireContext(), "All fields must be filled out!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            try {
//                Timestamp updatedTimestamp = new Timestamp(
//                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(updatedEventDate)
//                );
//
//                Map<String, Object> updatedData = new HashMap<>();
//                updatedData.put("eventName", updatedEventName);
//                updatedData.put("eventDate", updatedTimestamp);
//                updatedData.put("eventDetails", updatedEventDetails);
//
//                db.collection("AllEvents").document(eventID)
//                        .update(updatedData)
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(requireContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
//                            fetchAllEvents();
//                            dialog.dismiss();
//                        })
//                        .addOnFailureListener(e -> {
//                            Log.e(TAG, "Error updating event: ", e);
//                            Toast.makeText(requireContext(), "Failed to update event.", Toast.LENGTH_SHORT).show();
//                        });
//            } catch (Exception e) {
//                Log.e(TAG, "Invalid date format", e);
//                Toast.makeText(requireContext(), "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        deleteButton.setOnClickListener(v -> {
//            deleteEvent(eventID);
//            int position = eventIDs.indexOf(eventID);
//            if (position != -1) {
//                eventList.remove(position);
//                eventIDs.remove(position);
//                eventAdapter.notifyDataSetChanged();
//            }
//
//            Toast.makeText(requireContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
//            dialog.dismiss();
//        });
//
//        backButton.setOnClickListener(v -> dialog.dismiss());
//
//        dialog.show();
//    }

    private void deleteEvent(String eventID) {
        db.collection("AllEvents").document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully deleted: " + eventID))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event: " + eventID, e));
    }
}
