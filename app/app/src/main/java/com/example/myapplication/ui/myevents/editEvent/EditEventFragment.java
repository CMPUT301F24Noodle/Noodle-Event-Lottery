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

        package com.example.myapplication.ui.myevents.editEvent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.ui.myevents.manageEvent.DisplayQRCodeFragment;
import com.example.myapplication.ui.myevents.manageEvent.ManageEventFragment;
import com.google.zxing.WriterException;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Authors: Erin-Marie, Nishchay
 * Fragment that appears when the user wants to edit an events details
 * TODO: make the event poster appear if the organizer submitted one, Apoorv: If thats ever done then set the view to visible if the user has a poster. (Don't change xml)
 */

public class EditEventFragment extends Fragment {

    private EditText eventNameEditText, eventLocationEditText, eventDateTimeEditText, eventDetailsEditText, eventWaitingListEditText;
    private TextView eventStatusTextView;
    private ImageView eventPosterView;
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
        eventPosterView = view.findViewById(R.id.event_poster);
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
            this.event = main.currentEvent;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        String eventName = event.getEventName();
        String eventLocation = event.getFacility().getLocation();
        String eventDateTime = dateFormat.format(Objects.requireNonNull(event.getEventDate()));
        String eventDetails = event.getEventDetails();
        String eventWaitingList;
        String eventStatus;

        if (event.getMaxEntrants() == -1){
            eventWaitingList = event.getWaitingListSize() + " entrants";
        } else {
           eventWaitingList = event.getWaitingListSize() + " / " + event.getMaxEntrants();
        }

        if (event.getEventOver() == Boolean.FALSE){
            eventStatus = "Event Lottery Open";
        } else {
            eventStatus = "Event Lottery Closed";
        }

        // Populate fields with data from the Bundle
        eventNameEditText.setText(eventName != null ? eventName : "");
        eventLocationEditText.setText(eventLocation != null ? "Location: " + eventLocation : "Location:");
        eventDateTimeEditText.setText(eventDateTime != null ? "Event Date: " + eventDateTime : "Event Date");
        eventDetailsEditText.setText(eventDetails != null ? eventDetails : "");
        eventWaitingListEditText.setText("Capacity: " + eventWaitingList);
        eventStatusTextView.setText("Status: " + eventStatus);

        //prep for the manage event page
        eventDB.getEventEntrants(event);
        eventDB.getEventDeclined(event);
        eventDB.getEventWinners(event);
        eventDB.getEventAccepted(event);

        // set the event to main activity so it can be retrieved there
        if (main != null) {
            main.currentEvent = event; // set the event!
        }

        // Set EditTexts as non-editable initially
        setFieldsEditable(false);

        // Edit button toggles fields to editable
        editButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_edit_event_details);
        });

        // Save button saves data and toggles back to non-editable mode
        saveButton.setOnClickListener(v -> {
            saveUpdatedEventData();
            setFieldsEditable(false);
        });

        manageEventButton.setOnClickListener(v -> {
            //Bundle manageArgs = new Bundle();
            //manageArgs.putSerializable("event", event);
            //manageArgs.putSerializable("eventDB", eventDB);
            //openManageEventFragment(manageArgs);

            // set event and eventDB in main
            if (main != null) {
                main.currentEvent = event;
                main.eventDB = eventDB;
            }

            Navigation.findNavController(v).navigate(R.id.nav_manage_event);
        });

        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // first, get the QR code
                String ID = event.getEventID();
                if(ID != null){
                    // if there is an event id
                    Event QRGenerator = new Event();
                    Bitmap QRCode = null;

                    // get the QR code
                    try {
                        QRCode = QRGenerator.generateQRCode(ID, 700, 700);

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
     * Author: Nishchay
     * Sets the edit ability of the text fields and toggles button visibility.
     * @param editable Boolean of whether the event should be in a editable state
     */
    private void setFieldsEditable(boolean editable) {
        eventNameEditText.setEnabled(editable);
        //eventLocationEditText.setEnabled(editable);
        eventDateTimeEditText.setEnabled(editable);
        eventDetailsEditText.setEnabled(editable);
        eventWaitingListEditText.setEnabled(editable);

        saveButton.setVisibility(editable ? View.VISIBLE : View.GONE);
        editButton.setVisibility(editable ? View.GONE : View.VISIBLE);
    }

    /**
     * Author: Nishchay
     * Edited: Erin-Marie
     * Saves updated event data back to Firebase.
     */
    private void saveUpdatedEventData() {
        String updatedEventName = eventNameEditText.getText().toString().trim();
        //String updatedEventLocation = eventLocationEditText.getText().toString().trim();
        String updatedEventDateTime = eventDateTimeEditText.getText().toString().trim();
        String updatedEventDetails = eventDetailsEditText.getText().toString().trim();
        String updatedEventWaitingList = eventWaitingListEditText.getText().toString().trim();

        // Log the updated data for debugging
        Log.d("EditEventFragment", "Updated Event Name: " + updatedEventName);
        //Log.d("EditEventFragment", "Updated Event Location: " + updatedEventLocation);
        Log.d("EditEventFragment", "Updated Event Date & Time: " + updatedEventDateTime);
        Log.d("EditEventFragment", "Updated Event Details: " + updatedEventDetails);
        Log.d("EditEventFragment", "Updated Event Waiting List: " + updatedEventWaitingList);


        if (event != null){
            event.setEventName(updatedEventName);
            //event.setEventLocation(updatedEventLocation);
            event.setEventDetails(updatedEventDetails);
            // Additional setters as necessary, e.g., eventDate and waiting list values

            eventDB.updateEvent(event); // Assuming this updates based on event ID
            Toast.makeText(getContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error: Event ID is missing.", Toast.LENGTH_SHORT).show();
        }
    }


}
