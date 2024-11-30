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

/**
 * Authors: Erin-Marie, Nishchay
 * Fragment that appears when the user wants to edit an events details
 * TODO: make the event poster appear if the organizer submitted one
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
        }

        // Retrieve data from arguments
        Bundle args = getArguments();
        if (args != null) {
            //eventId = args.getString("event_id");
            event = (Event) args.getSerializable("event");
            //eventId = eventDB.getEvent().getEventID();
            String eventName = args.getString("event_name");
            //String eventLocation = args.getString("event_location");
            String eventLocation = event.getFacility().getFacilityName();
            String eventDateTime = args.getString("event_date_time");
            String eventDetails = args.getString("event_details");
            String eventWaitingList = args.getString("event_waiting_list");
            String eventStatus = args.getString("event_status");


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



            //TODO make the event poster show up
            // // eventPosterView.setImageResource(null);
            //eventWaitingListEditText.setText(eventWaitingList != null ? eventWaitingList : "");
            //eventStatusTextView.setText(eventStatus != null ? eventStatus: "");

            // Log the data for debugging
            Log.d("EditEventFragment", "Event ID: " + eventId);
            Log.d("EditEventFragment", "Event Name: " + eventName);
            Log.d("EditEventFragment", "Event Location: " + eventLocation);
            Log.d("EditEventFragment", "Event Date & Time: " + eventDateTime);
            Log.d("EditEventFragment", "Event Details: " + eventDetails);
            //Log.d("EditEventFragment", "Event Waiting List: " + eventWaitingList);
        } else {
            Toast.makeText(getContext(), "No event data provided", Toast.LENGTH_SHORT).show();
        }

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
            Bundle manageArgs = new Bundle();
            manageArgs.putSerializable("event", event);
            manageArgs.putSerializable("eventDB", eventDB);
            openManageEventFragment(manageArgs);
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
                        QRCode = QRGenerator.generateQRCode(ID, 300, 300);

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
     * Author: Erin-Marie
     * Opens the AddEventsFragment when the user selects the Manage Event button
     * @param manageArgs which is a bundle of arguments to be passed to the new fragment
     */
    private void openManageEventFragment(Bundle manageArgs) {
        // Create a new instance of AddEventsFragment
        ManageEventFragment addManageEventFragment = new ManageEventFragment();
        addManageEventFragment.setArguments(manageArgs);

        // Begin the Fragment transaction
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, addManageEventFragment); // Ensure this ID matches your main container ID
        transaction.addToBackStack(null); // Adds the transaction to the back stack
        transaction.commit();
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
