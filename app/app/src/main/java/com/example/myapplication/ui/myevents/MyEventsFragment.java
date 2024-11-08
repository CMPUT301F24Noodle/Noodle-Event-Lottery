package com.example.myapplication.ui.myevents;

import android.os.Bundle;
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

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMyeventsBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.database.EventDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class MyEventsFragment extends Fragment {

    private FragmentMyeventsBinding binding;
    private ArrayList<String> eventNamesList;
    private ArrayAdapter<String> adapter;

    private EventDB eventDB;
    private UserProfile currentUserProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        assert main != null;

        this.eventDB = main.eventDB;
        this.currentUserProfile = main.user;

        // Initialize the ListView with a simple adapter
        eventNamesList = new ArrayList<>();
        ListView listView = binding.createdEventList;
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, eventNamesList);
        listView.setAdapter(adapter);

        // Fetch events for the current user
        fetchUserEvents();

        // Set up the FloatingActionButton click listener
        FloatingActionButton fab = binding.createEventButton;
        fab.setOnClickListener(v -> openAddEventsFragment());

        return root;
    }

    private void fetchUserEvents() {
        ArrayList<DocumentReference> myEventRefs = currentUserProfile.getMyOrgEvents();

        for (DocumentReference eventRef : myEventRefs) {
            // Use the asynchronous getEvent method with OnSuccessListener
            eventDB.getEvent(eventRef, new OnSuccessListener<Event>() {
                @Override
                public void onSuccess(Event event) {
                    if (event != null) {
                        eventNamesList.add(event.getEventName());  // Add the event name to the list
                        adapter.notifyDataSetChanged(); // Update the ListView after each event is added
                    } else {
                        Toast.makeText(getContext(), "Failed to load an event.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void openAddEventsFragment() {
        // Create a new instance of AddEventsFragment
        AddEventsFragment addEventsFragment = new AddEventsFragment();

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
