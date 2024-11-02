package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.QRFragment;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the ListView
        ListView listView = binding.scannedEventList;

        // Sample data for the list (Heading and two Subheadings)
        List<ListItem> sampleList = new ArrayList<>();
        sampleList.add(new ListItem("Event Name", "Event Date", "Event Time", "Organizer"));
        sampleList.add(new ListItem("Event DOS", "Stuff", "Why are we here?", "Sleepy Rn"));

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
