package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventList;
    private MyEventsListArrayAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");

        // Initialize the listView
        ListView listView = binding.scannedEventList;

        // Initialize the event list and adapter
        eventList = new ArrayList<>();
        adapter = new MyEventsListArrayAdapter(requireContext(), eventList);
        listView.setAdapter(adapter);

        // load events from Firestore
        loadEventsFromFirestore();

        return root;
    }

    private void loadEventsFromFirestore() {
        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();
            eventList.addAll(queryDocumentSnapshots.toObjects(Event.class));
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Author: Sam Lee
     * Add a new event to the list and Firestore
     * 
     * @param event the event to add
     */
    private void addNewEvent(Event event) {
        // Add the event to the local list
        eventList.add(event);
        adapter.notifyDataSetChanged();

        // Add the event to the Firestore collection
        eventsRef.document(event.getEventName()).set(event)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"));
    }

    /**
     * Author: Sam Lee
     * Delete an event from the list and Firestore
     * 
     * @param event
     */
    private void deleteEvent(Event event) {
        // Remove the event from the local list
        eventList.remove(event);
        adapter.notifyDataSetChanged();

        // Delete the event from the Firestore collection
        eventsRef.document(event.getEventName()).delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully deleted!"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
