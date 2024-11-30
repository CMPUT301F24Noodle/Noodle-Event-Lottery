package com.example.myapplication.ui.myevents.editEvent;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.ui.myevents.ManagePosterFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class EditEventPosterDetailsFragment  extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText eventNameEditText, eventLocationEditText, dateDayEditText, dateMonthEditText, dateYearEditText;
    private EditText eventDetailsEditText, contactNumberEditText, maxParticipantsEditText, waitingListLimitEditText;
    private EditText eventTimeHour, eventTimeMinute;
    private Button addPosterButton, saveButton;
    private TextView currentStatusTextView, removeActionTextView, eventTimeColon;
    private ImageView posterImageView;
    private Switch geoLocationSwitch;
    private Uri selectedImageUri;

    private DBConnection connection;
    private EventDB eventDB;
    private UserProfile currentUserProfile;

    Event event; // the event that will be made


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_create_new_event, container, false);

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            this.connection = main.connection;
            this.currentUserProfile = main.user;
            this.eventDB = main.eventDB;
            this.event = main.currentEvent; // TODO this SHOULD solve the issue of getting event without bundles.
        }

        initializeViews(view);
        setButtonListeners();

        return view;
    }

    /**
     * Initializes views for event details input fields and buttons.
     * @param view the fragment view containing UI elements
     */
    private void initializeViews(View view) {
        eventNameEditText = view.findViewById(R.id.event_name_edit);
        eventLocationEditText = view.findViewById(R.id.event_location_edit);
        dateDayEditText = view.findViewById(R.id.date_picker_DD);
        dateMonthEditText = view.findViewById(R.id.date_picker_MM);
        dateYearEditText = view.findViewById(R.id.date_picker_YY);
        eventDetailsEditText = view.findViewById(R.id.textbox_detail);
        contactNumberEditText = view.findViewById(R.id.contact_num);
        maxParticipantsEditText = view.findViewById(R.id.max_participants);
        waitingListLimitEditText = view.findViewById(R.id.waiting_list_limit);

        // TODO THIS WASNT USED AT ALL IN ADDEVENTS????
        eventTimeHour = view.findViewById(R.id.time_picker_hh);
        eventTimeMinute = view.findViewById(R.id.time_picker_mm);
        eventTimeColon = view.findViewById(R.id.colon);


        addPosterButton = view.findViewById(R.id.add_poster_button);
        saveButton = view.findViewById(R.id.save_button);
        currentStatusTextView = view.findViewById(R.id.current_status);
        removeActionTextView = view.findViewById(R.id.remove_action);

        geoLocationSwitch = view.findViewById(R.id.geolocation_toggle);

        // and then make all the not relevant fields GONE or unchangeable
        eventNameEditText.setEnabled(false);
        eventLocationEditText.setVisibility(View.GONE);
        dateDayEditText.setVisibility(View.GONE);
        dateMonthEditText.setVisibility(View.GONE);
        dateYearEditText.setVisibility(View.GONE);
        contactNumberEditText.setVisibility(View.GONE);
        maxParticipantsEditText.setVisibility(View.GONE);
        waitingListLimitEditText.setVisibility(View.GONE);
        eventTimeColon.setVisibility(View.GONE);
        eventTimeHour.setVisibility(View.GONE);
        eventTimeMinute.setVisibility(View.GONE);


        // set existing text
        eventNameEditText.setText(event.getEventName());
        eventDetailsEditText.setText(event.getEventDetails());


    }

    /**
     * Sets button click listeners for adding/removing poster and saving event details.
     */
    private void setButtonListeners() {


        addPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManagePosterFragment PFragment = new ManagePosterFragment();
                PFragment.setEvent(event);
                PFragment.setEventDB(eventDB);

                // Navigate to the fragment
                PFragment.show(getParentFragmentManager(), "PosterManagementFragment");

            }
        });

        saveButton.setOnClickListener(v -> saveEventDetails());
    }


    /**
     * Validates and gathers input data, creates an Event object, and saves it to Firebase.
     * Navigates to EditEventFragment if the save is successful.
     */
    private void saveEventDetails() {
        String eventName = eventNameEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        Integer maxParticipants = null;
        String maxEntrants = waitingListLimitEditText.getText().toString().trim();
        String eventDetails = eventDetailsEditText.getText().toString().trim();



        if (eventName.isEmpty() || eventLocation.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //Set all the event data
        event.setEventName(eventName);

        if (!eventDetails.isEmpty()) {
            event.setEventDetails(eventDetails);
        }

        try {
            maxParticipants = Integer.parseInt(maxParticipantsEditText.getText().toString());
            event.setMaxParticipants(maxParticipants);
        } catch (NumberFormatException e) {
            CharSequence text = "Your event must a maximum number of participants";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return; //force them to go back and add one
        }

        try {
            Integer maxWait = Integer.parseInt(waitingListLimitEditText.getText().toString());
            event.setMaxEntrants(maxWait);
        } catch (NumberFormatException a) {
            Integer maxWait = -1;
            event.setMaxEntrants(maxWait);
        }


        event.setEventDate(new Date());

        event.setContact(contactNumberEditText.getText().toString().trim());

        event.setFacility(currentUserProfile.getFacility());

        if (geoLocationSwitch.isChecked()) {
            event.setGeoLocation(Boolean.TRUE);
        } else {
            event.setGeoLocation(Boolean.FALSE);
        }

        if (selectedImageUri != null) {
            event.setEventPoster(selectedImageUri.toString());
        }

        event.setOrganizer(currentUserProfile);
        event.setOrganizerRef(currentUserProfile.getDocRef());

        event.eventOver = Boolean.FALSE;

        try {
            eventDB.addEvent(event);
            currentUserProfile.addOrgEvent(event);
            Toast.makeText(getContext(), "Event saved successfully!", Toast.LENGTH_SHORT).show();
            clearFields();

        } catch (Exception e) {
            CharSequence sorry = "Event details could not be saved, please try again.";
            Toast.makeText(getContext(), sorry, Toast.LENGTH_SHORT).show();
            return;
        }



        // Prepare date formatting for passing to EditEventFragment

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getEventDate());

        // Create arguments to pass to EditEventFragment
        Bundle args = new Bundle();
        Event newEvent = event;
        args.putSerializable("event", newEvent);
        //args.putString("event_id", newEvent.getEventID());
        args.putString("event_name", eventName);
        args.putString("event_location", eventLocation);
        args.putString("event_date_time", formattedDate);
        args.putString("event_details", newEvent.getEventDetails());
        if (event.getEventOver() == Boolean.FALSE){
            args.putString("event_status", "Event Lottery Open");
        } else {
            args.putString("event_status", "Event Lottery Closed");
        }

        if (event.getMaxEntrants() == -1){
            args.putString("event_waiting_list", event.getWaitingListSize() + " entrants");
        } else {
            args.putString("event_waiting_list", event.getWaitingListSize() + " / " + event.getMaxEntrants());
        }


        EditEventFragment editEventFragment = new EditEventFragment();
        editEventFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, editEventFragment)
                .addToBackStack(null)
                .commit();

    }

    /**
     * Clears all input fields after a successful event creation.
     */
    private void clearFields() {
        eventNameEditText.setText("");
        eventLocationEditText.setText("");
        dateDayEditText.setText("");
        dateMonthEditText.setText("");
        dateYearEditText.setText("");
        eventDetailsEditText.setText("");
        contactNumberEditText.setText("");
        maxParticipantsEditText.setText("");
        waitingListLimitEditText.setText("");
        currentStatusTextView.setText("Current: None");
        if (posterImageView != null) {
            posterImageView.setImageResource(0);
        }
    }


}

