package com.example.myapplication.ui.myevents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

public class EditEventFragment extends Fragment {

    private EditText eventNameEditText, eventLocationEditText, eventDateTimeEditText, eventDetailsEditText, eventWaitingListEditText;
    private Button editButton, saveButton;
    private DBConnection connection;
    private EventDB eventDB;
    private UserProfile currentUserProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_event_organizer, container, false);

        // Initialize EditText fields
        eventNameEditText = view.findViewById(R.id.event_name);
        eventLocationEditText = view.findViewById(R.id.event_location);
        eventDateTimeEditText = view.findViewById(R.id.event_date_time);
        eventDetailsEditText = view.findViewById(R.id.event_details);
        eventWaitingListEditText = view.findViewById(R.id.event_waiting_list);

        // Initialize Buttons
        editButton = view.findViewById(R.id.edit_event);
        saveButton = view.findViewById(R.id.save_button);

        // Retrieve data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String eventName = args.getString("event_name");
            String eventLocation = args.getString("event_location");
            String eventDateTime = args.getString("event_date_time");
            String eventDetails = args.getString("event_details");
            String eventWaitingList = args.getString("event_waiting_list");

            // Log retrieved values to check for any nulls
            Log.d("EditEventFragment", "Event Name: " + eventName);
            Log.d("EditEventFragment", "Event Location: " + eventLocation);
            Log.d("EditEventFragment", "Event Date & Time: " + eventDateTime);
            Log.d("EditEventFragment", "Event Details: " + eventDetails);
            Log.d("EditEventFragment", "Event Waiting List: " + eventWaitingList);

            // Set initial text to EditTexts with null check
            eventNameEditText.setText(eventName != null ? eventName : "");
            eventLocationEditText.setText(eventLocation != null ? eventLocation : "");
            eventDateTimeEditText.setText(eventDateTime != null ? eventDateTime : "");
            eventDetailsEditText.setText(eventDetails != null ? eventDetails : "");
            eventWaitingListEditText.setText(eventWaitingList != null ? eventWaitingList : "");
        }

        // Set EditTexts as non-editable initially
        setFieldsEditable(false);

        // Edit button toggles fields to editable
        editButton.setOnClickListener(v -> setFieldsEditable(true));

        // Save button saves data and toggles back to non-editable mode
        saveButton.setOnClickListener(v -> {
            saveUpdatedEventData();
            setFieldsEditable(false);
        });

        return view;
    }

    // Method to toggle fields editable or non-editable
    private void setFieldsEditable(boolean editable) {
        eventNameEditText.setEnabled(editable);
        eventLocationEditText.setEnabled(editable);
        eventDateTimeEditText.setEnabled(editable);
        eventDetailsEditText.setEnabled(editable);
        eventWaitingListEditText.setEnabled(editable);

        saveButton.setVisibility(editable ? View.VISIBLE : View.GONE);
        editButton.setVisibility(editable ? View.GONE : View.VISIBLE);
    }

    // Save updated data to Firebase or perform other actions as needed
    private void saveUpdatedEventData() {
        String updatedEventName = eventNameEditText.getText().toString().trim();
        String updatedEventLocation = eventLocationEditText.getText().toString().trim();
        String updatedEventDateTime = eventDateTimeEditText.getText().toString().trim();
        String updatedEventDetails = eventDetailsEditText.getText().toString().trim();
        String updatedEventWaitingList = eventWaitingListEditText.getText().toString().trim();

        Log.d("EditEventFragment", "Updated Event Name: " + updatedEventName);
        Log.d("EditEventFragment", "Updated Event Location: " + updatedEventLocation);
        Log.d("EditEventFragment", "Updated Event Date & Time: " + updatedEventDateTime);
        Log.d("EditEventFragment", "Updated Event Details: " + updatedEventDetails);
        Log.d("EditEventFragment", "Updated Event Waiting List: " + updatedEventWaitingList);

        Toast.makeText(getContext(), "Event data updated!", Toast.LENGTH_SHORT).show();
    }
}