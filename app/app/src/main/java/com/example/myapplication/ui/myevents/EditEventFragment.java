/**
 * Author: Nishchay Ranjan
 *
 * This fragment allows users to edit event details that are stored in Firebase.
 * - The fragment receives event details through a Bundle and displays them in editable text fields.
 * - Users can modify the event details, which are then updated in Firebase when saved.
 *
 * Important Components:
 * - onCreateView: Initializes UI elements, retrieves event data from arguments, and displays them.
 * - setFieldsEditable: Controls the editability of text fields.
 * - saveUpdatedEventData: Saves modified event data back to Firebase using the provided event ID.
 */

        package com.example.myapplication.ui.myevents;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.zxing.WriterException;

public class EditEventFragment extends Fragment {

    private EditText eventNameEditText, eventLocationEditText, eventDateTimeEditText, eventDetailsEditText, eventWaitingListEditText;
    private TextView eventStatusTextView;
    private Button editButton, saveButton, manageEventButton, QRButton;
    private DBConnection connection;
    private EventDB eventDB;
    private Event event;
    private UserProfile currentUserProfile;
    private String eventId; // Stores event ID

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
        eventStatusTextView = view.findViewById((R.id.event_status));

        // Initialize Buttons
        editButton = view.findViewById(R.id.edit_event);
        saveButton = view.findViewById(R.id.save_button);
        manageEventButton = view.findViewById(R.id.manage_event);
        QRButton = view.findViewById(R.id.generate_qr);

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            this.connection = main.connection;
            this.eventDB = main.eventDB;
            this.currentUserProfile = main.user;
        }

        // Retrieve data from arguments
        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString("event_id");
            event = (Event) args.getSerializable("event");
            String eventName = args.getString("event_name");
            String eventLocation = args.getString("event_location");
            String eventDateTime = args.getString("event_date_time");
            String eventDetails = args.getString("event_details");
            String eventWaitingList = args.getString("event_waiting_list");
            String eventStatus = args.getString("event_status");

            // Populate fields with data from the Bundle
            eventNameEditText.setText(eventName != null ? eventName : "");
            eventLocationEditText.setText(eventLocation != null ? eventLocation : "");
            eventDateTimeEditText.setText(eventDateTime != null ? eventDateTime : "");
            eventDetailsEditText.setText(eventDetails != null ? eventDetails : "");
            eventWaitingListEditText.setText(eventWaitingList != null ? eventWaitingList : "");
            eventStatusTextView.setText(eventStatus != null ? eventStatus: "");

            // Log the data for debugging
            Log.d("EditEventFragment", "Event ID: " + eventId);
            Log.d("EditEventFragment", "Event Name: " + eventName);
            Log.d("EditEventFragment", "Event Location: " + eventLocation);
            Log.d("EditEventFragment", "Event Date & Time: " + eventDateTime);
            Log.d("EditEventFragment", "Event Details: " + eventDetails);
            Log.d("EditEventFragment", "Event Waiting List: " + eventWaitingList);
        } else {
            Toast.makeText(getContext(), "No event data provided", Toast.LENGTH_SHORT).show();
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

        manageEventButton.setOnClickListener(v -> {
            Bundle manageArgs = new Bundle();
            manageArgs.putSerializable("event", event);
        });

        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // first, get the QR code
                if(eventId != null){
                    // if there is an event id
                    Event QRGenerator = new Event();
                    Bitmap QRCode = null;

                    // get the QR code
                    try {
                        QRCode = QRGenerator.generateQRCode(eventId, 300, 300);

                    } catch (WriterException e) {
                        throw new RuntimeException(e);
                    }

                    // ensure that the QR code didn't mess up and throw a RuntimeException
                    if(QRCode == null){
                        Toast.makeText(getContext(), "There was an error generating the QR code", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        DisplayQRCodeFragment QRFragment = new DisplayQRCodeFragment();
                        QRFragment.setQRCode(QRCode);

                        // Navigate to the fragment
                        QRFragment.show(getParentFragmentManager(), "QrPopupFragment");
                    }

                }
                else{
                    // the event doesn't have an event ID
                    Toast.makeText(getContext(), "Please wait while event is fully saved to database", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    /**
     * Sets the editability of the text fields and toggles button visibility.
     * @param editable Whether fields should be editable
     */
    private void setFieldsEditable(boolean editable) {
        eventNameEditText.setEnabled(editable);
        eventLocationEditText.setEnabled(editable);
        eventDateTimeEditText.setEnabled(editable);
        eventDetailsEditText.setEnabled(editable);
        eventWaitingListEditText.setEnabled(editable);

        saveButton.setVisibility(editable ? View.VISIBLE : View.GONE);
        editButton.setVisibility(editable ? View.GONE : View.VISIBLE);
    }

    /**
     * Saves updated event data back to Firebase.
     */
    private void saveUpdatedEventData() {
        String updatedEventName = eventNameEditText.getText().toString().trim();
        String updatedEventLocation = eventLocationEditText.getText().toString().trim();
        String updatedEventDateTime = eventDateTimeEditText.getText().toString().trim();
        String updatedEventDetails = eventDetailsEditText.getText().toString().trim();
        String updatedEventWaitingList = eventWaitingListEditText.getText().toString().trim();

        // Log the updated data for debugging
        Log.d("EditEventFragment", "Updated Event Name: " + updatedEventName);
        Log.d("EditEventFragment", "Updated Event Location: " + updatedEventLocation);
        Log.d("EditEventFragment", "Updated Event Date & Time: " + updatedEventDateTime);
        Log.d("EditEventFragment", "Updated Event Details: " + updatedEventDetails);
        Log.d("EditEventFragment", "Updated Event Waiting List: " + updatedEventWaitingList);

        if (eventId != null) {
            Event updatedEvent = new Event();
            updatedEvent.setEventID(eventId);
            updatedEvent.setEventName(updatedEventName);
            updatedEvent.setEventLocation(updatedEventLocation);
            updatedEvent.setEventDetails(updatedEventDetails);
            // Additional setters as necessary, e.g., eventDate and waiting list values

            eventDB.updateEvent(updatedEvent); // Assuming this updates based on event ID
            Toast.makeText(getContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error: Event ID is missing.", Toast.LENGTH_SHORT).show();
        }
    }
}
