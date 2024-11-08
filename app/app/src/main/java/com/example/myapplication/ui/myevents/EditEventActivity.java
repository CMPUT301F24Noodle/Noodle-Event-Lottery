package com.example.myapplication.ui.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class EditEventActivity extends AppCompatActivity {

    private EditText eventNameEditText, eventLocationEditText, eventDateTimeEditText, eventDetailsEditText, eventWaitingListEditText;
    private Button editButton, saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_organizer);

        // Initialize EditText fields
        eventNameEditText = findViewById(R.id.event_name);
        eventLocationEditText = findViewById(R.id.event_location);
        eventDateTimeEditText = findViewById(R.id.event_date_time);
        eventDetailsEditText = findViewById(R.id.event_details);
        eventWaitingListEditText = findViewById(R.id.event_waiting_list);

        // Initialize Buttons
        editButton = findViewById(R.id.edit_event);
        saveButton = findViewById(R.id.save_button);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("event_name");
        String eventLocation = intent.getStringExtra("event_location");
        String eventDateTime = intent.getStringExtra("event_date_time"); // Ensure this is a formatted string
        String eventDetails = intent.getStringExtra("event_details");
        String eventWaitingList = intent.getStringExtra("event_waiting_list");

        // Log retrieved values to check for any nulls
        Log.d("EditEventActivity", "Event Name: " + eventName);
        Log.d("EditEventActivity", "Event Location: " + eventLocation);
        Log.d("EditEventActivity", "Event Date & Time: " + eventDateTime);
        Log.d("EditEventActivity", "Event Details: " + eventDetails);
        Log.d("EditEventActivity", "Event Waiting List: " + eventWaitingList);

        // Set initial text to EditTexts with null check
        eventNameEditText.setText(eventName != null ? eventName : "");
        eventLocationEditText.setText(eventLocation != null ? eventLocation : "");
        eventDateTimeEditText.setText(eventDateTime != null ? eventDateTime : "");
        eventDetailsEditText.setText(eventDetails != null ? eventDetails : "");
        eventWaitingListEditText.setText(eventWaitingList != null ? eventWaitingList : "");

        // Set EditTexts as non-editable initially
        setFieldsEditable(false);

        // Edit button toggles fields to editable
        editButton.setOnClickListener(v -> setFieldsEditable(true));

        // Save button saves data and toggles back to non-editable mode
        saveButton.setOnClickListener(v -> {
            saveUpdatedEventData();
            setFieldsEditable(false);
        });
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
        // Retrieve updated values from EditTexts
        String updatedEventName = eventNameEditText.getText().toString().trim();
        String updatedEventLocation = eventLocationEditText.getText().toString().trim();
        String updatedEventDateTime = eventDateTimeEditText.getText().toString().trim();
        String updatedEventDetails = eventDetailsEditText.getText().toString().trim();
        String updatedEventWaitingList = eventWaitingListEditText.getText().toString().trim();

        // Log the updated values
        Log.d("EditEventActivity", "Updated Event Name: " + updatedEventName);
        Log.d("EditEventActivity", "Updated Event Location: " + updatedEventLocation);
        Log.d("EditEventActivity", "Updated Event Date & Time: " + updatedEventDateTime);
        Log.d("EditEventActivity", "Updated Event Details: " + updatedEventDetails);
        Log.d("EditEventActivity", "Updated Event Waiting List: " + updatedEventWaitingList);

        // Show a success message for now
        Toast.makeText(this, "Event data updated!", Toast.LENGTH_SHORT).show();

        // Update Firebase or local storage with new values (implement your update logic here)
    }
}
