//package com.example.myapplication.ui.myevents;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.myapplication.R;
//
//public class ViewEventOrganizerFragment extends Fragment {
//
//    private MyEventsViewModel myEventsViewModel;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // Inflate the XML layout for this fragment
//        View root = inflater.inflate(R.layout.view_event_organizer, container, false);
//
//        // Initialize the ViewModel
//        myEventsViewModel = new ViewModelProvider(this).get(MyEventsViewModel.class);
//
//        // Get references to the views in the layout
//        TextView eventName = root.findViewById(R.id.event_name);
//        ImageView eventPoster = root.findViewById(R.id.event_poster);
//        TextView eventDateTime = root.findViewById(R.id.event_date_time);
//        TextView eventLocation = root.findViewById(R.id.event_location);
//        TextView eventDetails = root.findViewById(R.id.event_details);
//        TextView eventWaitingList = root.findViewById(R.id.event_waiting_list);
//        TextView eventStatus = root.findViewById(R.id.event_status);
//
//        Button generateQrButton = root.findViewById(R.id.generate_qr);
//        Button manageEventButton = root.findViewById(R.id.manage_event);
//        Button editEventButton = root.findViewById(R.id.edit_event);
//        Button deleteEventButton = root.findViewById(R.id.delete_event);
//
//        // Example usage of ViewModel data (if needed)
//        myEventsViewModel.getText().observe(getViewLifecycleOwner(), text -> {
//            // Example: Set some text from ViewModel
//            eventName.setText(text); // This would display "This is gallery fragment" initially
//        });
//
//        // Set listeners for buttons (if needed)
//        generateQrButton.setOnClickListener(v -> {
//            // Handle QR code generation
//        });
//
//        manageEventButton.setOnClickListener(v -> {
//            // Handle event management
//        });
//
//        editEventButton.setOnClickListener(v -> {
//            // Handle event editing
//        });
//
//        deleteEventButton.setOnClickListener(v -> {
//            // Handle event deletion
//        });
//
//        return root;
//    }
//}
