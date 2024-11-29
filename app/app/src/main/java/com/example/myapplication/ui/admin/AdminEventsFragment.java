package com.example.myapplication.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminEventsFragment extends Fragment {

    private static final String TAG = "AdminEventsFragment";

    public AdminEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.item_admin_event, container, false);

        // Fetch and log events data
        fetchAllEvents();

        return rootView;
    }

    private void fetchAllEvents() {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference the "AllEvents" collection
        db.collection("AllEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Loop through each document in the collection
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Extract the fields
                            String eventDate = document.getString("eventDate");
                            String eventDetails = document.getString("eventDetails");
                            String eventName = document.getString("eventName");

                            // Log the data
                            Log.d(TAG, "Event Name: " + eventName);
                            Log.d(TAG, "Event Date: " + eventDate);
                            Log.d(TAG, "Event Details: " + eventDetails);
                        }
                    } else {
                        // Log an error if the query fails
                        Log.e(TAG, "Error fetching events: ", task.getException());
                    }
                });
    }
}
