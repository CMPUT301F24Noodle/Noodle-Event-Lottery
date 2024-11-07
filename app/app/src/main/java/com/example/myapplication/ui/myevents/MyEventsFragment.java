package com.example.myapplication.ui.myevents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMyeventsBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.ui.myevents.AddEventsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MyEventsFragment extends Fragment {

    private FragmentMyeventsBinding binding;
    private ArrayList<Event> eventList;
    private OrganizedEventArrayAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyEventsViewModel myEventViewModel = new ViewModelProvider(this)
                .get(MyEventsViewModel.class);

        binding = FragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the listView
        eventList = new ArrayList<>();
        ListView listView = binding.createdEventList;

        adapter = new OrganizedEventArrayAdapter(requireContext(), eventList);
        listView.setAdapter(adapter);

        // Set up the FloatingActionButton click listener
        FloatingActionButton fab = binding.createEventButton;
        fab.setOnClickListener(v -> openAddEventsFragment());

        return root;
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
