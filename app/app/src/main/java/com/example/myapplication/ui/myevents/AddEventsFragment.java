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
import android.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEventsFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText eventNameEditText, eventLocationEditText, dateDayEditText, dateMonthEditText, dateYearEditText;
    private EditText eventDetailsEditText, contactNumberEditText, maxParticipantsEditText, waitingListLimitEditText;
    private Button addPosterButton, saveButton;
    private TextView currentStatusTextView, removeActionTextView, dateRangeTextTo;
    private ImageView posterImageView;
    private Switch geoLocationSwitch, repeatingSwitch;
    private Uri selectedImageUri;
    private EditText endDateDD, endDateMM, endDateYY;
    private EditText regStartDD, regStartMM, regStartYY, regEndDD, regEndMM, regEndYY;
    private EditText eventTimeViewMM, eventTimeViewHH;
    private CheckBox repeatMonView, repeatTueView, repeatWedView, repeatThurView, repeatFriView, repeatSatView, repeatSunView;

    private DBConnection connection;
    private EventDB eventDB;
    private UserProfile currentUserProfile;

    Event event; // the event that will be made

    LinearLayout repeatingWeek;


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

        //assume the event is repeating at first
        repeatingSwitch.setChecked(true);
        event.setRepeating(Boolean.TRUE);

        setOnClickListeners();

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
        repeatingSwitch = view.findViewById(R.id.repeating_event_toggle);

        repeatingWeek = view.findViewById(R.id.repeat_week_layout);
        endDateDD = view.findViewById(R.id.end_date_picker_DD);
        endDateMM = view.findViewById(R.id.end_date_picker_MM);
        endDateYY = view.findViewById(R.id.end_date_picker_YY);
        dateRangeTextTo = view.findViewById(R.id.date_range_text_to);

        regStartDD = view.findViewById(R.id.regstart_date_picker_DD);
        regStartMM = view.findViewById(R.id.regstart_date_picker_MM);
        regStartYY = view.findViewById(R.id.regstart_date_picker_YY);

        regEndDD = view.findViewById(R.id.regend_date_picker_DD);
        regEndMM = view.findViewById(R.id.regend_date_picker_MM);
        regEndYY = view.findViewById(R.id.regend_date_picker_YY);

        eventTimeViewMM = view.findViewById(R.id.time_picker_mm);
        eventTimeViewHH = view.findViewById(R.id.time_picker_hh);

        repeatMonView = view.findViewById(R.id.repeat_monday);
        repeatTueView = view.findViewById(R.id.repeat_tuesday);
        repeatWedView = view.findViewById(R.id.repeat_wednesday);
        repeatThurView = view.findViewById(R.id.repeat_thursday);
        repeatFriView = view.findViewById(R.id.repeat_friday);
        repeatSatView = view.findViewById(R.id.repeat_saturday);
        repeatSunView = view.findViewById(R.id.repeat_sunday);


    }

    /**
     * Sets button/toggle click listeners for adding/removing poster and saving event details.
     */
    private void setOnClickListeners() {


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

        repeatingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // if toggled on: make repeating stuff visible!
                    repeatingWeek.setVisibility(View.VISIBLE);
                    endDateYY.setVisibility(View.VISIBLE);
                    endDateMM.setVisibility(View.VISIBLE);
                    endDateDD.setVisibility(View.VISIBLE);
                    dateRangeTextTo.setVisibility(TextView.VISIBLE);

                    event.setRepeating(Boolean.TRUE);

                } else {
                    // if toggled off, make repeating stuff invisible!
                    repeatingWeek.setVisibility(View.GONE);
                    endDateYY.setVisibility(View.GONE);
                    endDateMM.setVisibility(View.GONE);
                    endDateDD.setVisibility(View.GONE);
                    dateRangeTextTo.setVisibility(TextView.GONE);

                    event.setRepeating(Boolean.FALSE);
                }
            }
        });
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

        // get info for all the dates
        String eventStartDay = dateDayEditText.getText().toString().trim();
        String eventStartMonth = dateMonthEditText.getText().toString().trim();
        String eventStartYear = dateYearEditText.getText().toString().trim();

        String eventEndDay = endDateDD.getText().toString().trim();
        String eventEndMonth = endDateMM.getText().toString().trim();
        String eventEndYear = endDateYY.getText().toString().trim();

        String regStartDay = regStartDD.getText().toString().trim();
        String regStartMonth = regStartMM.getText().toString().trim();
        String regStartYear = regStartYY.getText().toString().trim();

        String regEndDay = regEndDD.getText().toString().trim();
        String regEndMonth = regEndMM.getText().toString().trim();
        String regEndYear = regEndYY.getText().toString().trim();

        String eventTimeMM = eventTimeViewMM.getText().toString().trim();
        String eventTimeHH = eventTimeViewHH.getText().toString().trim();
        String eventTime = eventTimeHH + ":" + eventTimeMM;

        if(event.isRepeating{
            List<String> repeatingDays = new ArrayList<String>();
            if(repeatMonView.isChecked()){
                repeatingDays.add("Mon");
            }
            if(repeatTueView.isChecked()){
                repeatingDays.add("Tue");
            }
            if(repeatWedView.isChecked()){
                repeatingDays.add("Wed");
            }
            if(repeatThurView.isChecked()){
                repeatingDays.add("Thur");
            }
            if(repeatFriView.isChecked()){
                repeatingDays.add("Fri");
            }
            if(repeatSatView.isChecked()){
                repeatingDays.add("Sat");
            }
            if(repeatSunView.isChecked()){
                repeatingDays.add("Sun");
            }
            event.setRepeatingDays(repeatingDays);
        }

        // run through all reasons to not let the event be saved
        if (eventName.isEmpty() || eventLocation.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in the event name and location", Toast.LENGTH_SHORT).show();
            return;
        }

        Date startDate = stringToDate(eventStartDay, eventStartMonth, eventStartYear);
        // if you could not make a valid date, dont save the event
        if(startDate == null){
            Toast.makeText(getContext(), "Please provide a valid start date for your event", Toast.LENGTH_SHORT).show();
            return;
        }

        Date endDate = null;
        if(event.getRepeating()){
            endDate = stringToDate(eventEndDay, eventEndMonth, eventEndYear);
            if(endDate == null){
                Toast.makeText(getContext(), "Please provide a valid end date for your event", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Date regStartDate = stringToDate(regStartDay, regStartMonth, regStartYear);
        if(regStartDate == null){
            Toast.makeText(getContext(), "Please provide a valid start registration date for you event", Toast.LENGTH_SHORT).show();
            return;
        }

        Date regEndDate = stringToDate(regEndDay, regEndMonth, regEndYear);
        if(regEndDate == null){
            Toast.makeText(getContext(), "Please provide a valid end registration date for you event", Toast.LENGTH_SHORT).show();
            return;
        }

        if(eventTimeMM.isEmpty() || eventTimeHH.isEmpty()){
            Toast.makeText(getContext(), "Please provide a time for your event", Toast.LENGTH_SHORT).show();
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


        event.setEventDate(startDate);
        event.setLotteryOpens(regStartDate);
        event.setLotteryCloses(regEndDate);
        event.setEventTime(eventTime);

        if(event.getRepeating()){
            event.setEventDateEnd(endDate);
        }

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

    /**
     * Converts a day, month and year string into a date object
     */
    public Date stringToDate(String day, String month, String year) {

        // Combine the strings into a date format
        String dateString = day + "-" + month + "-" + year;

        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            // Parse the string into a Date object
            Date date = dateFormat.parse(dateString);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }




}
