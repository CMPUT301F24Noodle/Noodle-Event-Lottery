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

public class AdminEventsFragment extends Fragment {

    private static final String TAG = "AdminEventsFragment";

    private ListView eventListView;
    private ArrayList<String> eventList;
    private ArrayList<String> eventIDs;
    private ArrayList<Event> fullEventList;
    private ArrayAdapter<Event> eventAdapter;
    private FirebaseFirestore db;

    public AdminEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_admin_listview, container, false);

        eventListView = rootView.findViewById(R.id.admin_item_list);
        eventList = new ArrayList<>();
        eventIDs = new ArrayList<>();
        fullEventList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        eventAdapter = new AdminEventArrayAdapter(requireContext(), fullEventList);

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
                        fullEventList.remove(position);
                        eventList.remove(position);
                        eventAdapter.notifyDataSetChanged();
                        assert delEvent != null;
                        db.collection("AllEvents").document(delEvent.getEventID()).delete();
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

    private void deleteEvent(String eventID) {
        db.collection("AllEvents").document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully deleted: " + eventID))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event: " + eventID, e));
    }
}
