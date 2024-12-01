/**
 * Author: Nishchay Ranjan
 *
 * This class represents the AddEventsFragment, where users can create a new event by providing details such as event name, location, date, and other relevant information.
 * - Users can add or remove a poster image for the event.
 * - Event details are saved in the database upon form submission.
 * - If the event is successfully created, the fragment navigates to the EditEventFragment for further modifications.
 *
 * Important Components:
 * - initializeViews: Sets up the UI elements and retrieves instances of DBConnection, EventDB, and UserProfile.
 * - setButtonListeners: Defines actions for the "Add Poster," "Remove Poster," and "Save" buttons.
 * - saveEventDetails: Gathers data from the input fields, creates an Event object, and attempts to save it to the database.
 * - clearFields: Clears the input fields after successful event creation.
 * - checkAndRequestPermissions: Checks or requests permission for accessing external storage.
 * - displaySelectedImage: Displays the selected poster image in the ImageView.
 */
package com.example.myapplication.ui.myevents;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.ui.myevents.editEvent.EditEventFragment;

import com.example.myapplication.ui.user_profile.ManageProfilePictureFragment;
import com.google.firebase.firestore.auth.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEventsFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText eventNameEditText, eventLocationEditText, dateDayEditText, dateMonthEditText, dateYearEditText;
    private EditText eventDetailsEditText, contactNumberEditText, maxParticipantsEditText, waitingListLimitEditText;
    private Button addPosterButton, saveButton;
    private TextView currentStatusTextView, removeActionTextView;
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
        }

        event = new Event();


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

        addPosterButton = view.findViewById(R.id.add_poster_button);
        saveButton = view.findViewById(R.id.save_button);
        currentStatusTextView = view.findViewById(R.id.current_status);
        removeActionTextView = view.findViewById(R.id.remove_action);

        geoLocationSwitch = view.findViewById(R.id.geolocation_toggle);

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

        saveButton.setOnClickListener(v -> saveEventDetails(v));
    }


    /**
     * Validates and gathers input data, creates an Event object, and saves it to Firebase.
     * Navigates to EditEventFragment if the save is successful.
     */
    private void saveEventDetails(View v) {
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
            eventDB.getUserOrgEvents(currentUserProfile);
            Toast.makeText(getContext(), "Event saved successfully!", Toast.LENGTH_SHORT).show();
            clearFields();

        } catch (Exception e) {
            CharSequence sorry = "Event details could not be saved, please try again.";
            Toast.makeText(getContext(), sorry, Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            main.currentEvent = event; // pass this in so fragment can access it
        }

        //Navigation.findNavController(v).navigate(R.id.nav_edit_event, args);
        Navigation.findNavController(v).navigate(R.id.nav_edit_event);

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
