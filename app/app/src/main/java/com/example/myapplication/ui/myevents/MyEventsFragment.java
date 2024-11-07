package com.example.myapplication.ui.myevents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentMyeventsBinding;
import com.example.myapplication.databinding.FragmentRegisteredEventsBinding;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.ui.registeredevents.RegisteredEventArrayAdapter;
import com.example.myapplication.ui.registeredevents.RegisteredEventViewModel;

import java.util.ArrayList;

//Erin-Marie: I am just adding this for testing, it is not actually functional, i just needed it so that i can write the MainActivity navigate test
//Nischay can replace this
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

        eventList = new ArrayList<Event>();

        ListView listView = binding.createdEventList;

        adapter = new OrganizedEventArrayAdapter(requireContext(), eventList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}