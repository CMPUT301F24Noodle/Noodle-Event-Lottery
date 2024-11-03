package com.example.myapplication.ui.registeredevents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentRegisteredEventsBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.ui.home.MyEventsListArrayAdapter;
import com.example.myapplication.ui.home.ListItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Who?
 * Editor: Sam Lee; I chaged the hard-coded version of  arrayadapter handling
 * so the arrayadapter take List<Event> instead of List<ListItem>
 */
public class RegisteredEventFragment extends Fragment {

    private FragmentRegisteredEventsBinding binding;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventList;
    private MyEventsListArrayAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RegisteredEventViewModel registeredEventViewModel =
                new ViewModelProvider(this).get(RegisteredEventViewModel.class);

            binding = FragmentRegisteredEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");

        // Initialize the listView
        ListView listView = binding.registeredEventList;

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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}