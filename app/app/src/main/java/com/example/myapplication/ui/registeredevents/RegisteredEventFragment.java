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
import com.example.myapplication.ui.home.CustomAdapter;
import com.example.myapplication.ui.home.ListItem;

import java.util.ArrayList;
import java.util.List;

public class RegisteredEventFragment extends Fragment {

        private FragmentRegisteredEventsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RegisteredEventViewModel registeredEventViewModel =
                new ViewModelProvider(this).get(RegisteredEventViewModel.class);

            binding = FragmentRegisteredEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = binding.registeredEventList;

        List<ListItem> sampleList = new ArrayList<>();
        sampleList.add(new ListItem("Event Alpha", "12 March 2023", "Event Time", "Organizer's Name"));
        sampleList.add(new ListItem("Event Beta", "12 April 2022", "Why are we here?", "Sleepy Rn"));

        // Set up the custom adapter
        CustomAdapter adapter = new CustomAdapter(requireContext(), sampleList);

        listView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}