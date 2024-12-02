package com.example.myapplication.ui.registeredevents;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.UserProfile;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Authors: Erin-Marie and Sam Lee
 * Activity for viewing and entering an event scanned from a QR code.
 *[US 01.06.01, US 01.06.02]
 */
public class ViewScannedEventFragment extends Fragment {

    private Event event;
    private DBConnection connection;
    private UserProfile user;
    private Boolean locationSharingAllowed = Boolean.FALSE; // Boolean for whether the entrant has allowed their geolocation to be shared
    private Boolean geolocationRequired; //Boolean for whether or not the event requires entrants geolocation
    private Boolean userCanEnter = Boolean.FALSE; //Boolean for whether or not the user has been approved to enter the event
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        assert inflater != null;
        View view = inflater.inflate(R.layout.view_event_user, container, false);

        // Initialize location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            this.connection = main.connection;
            this.event = main.scannedEvent;
            geolocationRequired = event.getGeoLocation();
            user = connection.getUser();

        }

        //initialize view interactables
        CheckBox confirmGeolocation = view.findViewById(R.id.location_sharing_checkbox);
        Button saveButton = view.findViewById(R.id.event_save_button);
        TextView switchText = view.findViewById(R.id.register_for_lottery_text);
        Switch enterEventSwitch = view.findViewById(R.id.lottery_switch);


        //Check that the user is not already in the event
        if (event.getEntrantsList().contains(user.getDocRef())){
            saveButton.setVisibility(View.GONE);
            enterEventSwitch.setVisibility(View.GONE);
            switchText.setVisibility(View.GONE);
            confirmGeolocation.setVisibility(View.GONE);
            Toast.makeText(getContext(), "You are already registered for this event", Toast.LENGTH_LONG).show();
        }

        //EVENT NAME/TITLE
        TextView eventName = view.findViewById(R.id.event_name);
        eventName.setText(event.getEventName());

        //EVENT LOCATION
        TextView eventLocation = view.findViewById(R.id.event_location);
        eventLocation.setText(event.getFacility().getFacilityName());

        //EVENT DATE AND TIME
        TextView eventDateTime = view.findViewById(R.id.event_date_time);
        Date eventDate = event.getEventDate();
        String eventDateString = null;
        if (eventDate != null) {
            eventDateString = eventDate.toString();
        }
        LocalDateTime newDate = LocalDateTime.parse(eventDateString, DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");

        String eventDateTimeText;

        TextView eventRepeatDaysView = view.findViewById(R.id.event_repeat);
        if(event.getRepeating()){
            List<String> repeatingDays = event.getRepeatingDays();

            // get proper event time range
            eventDateTimeText = dateToString(event.getEventDate())
                    + " to "
                    + dateToString(event.getEventDateEnd())
                    +" at "
                    + event.getEventTime();

            // and then populate repeating days
            String repeatDaysText = "Repeats:";
            if(repeatingDays.isEmpty() || repeatingDays == null){
                eventRepeatDaysView.setVisibility(View.GONE);
            }
            else{
                // Using for-each loop to iterate through the list
                for (String day : repeatingDays) {
                    repeatDaysText = repeatDaysText + " " + day;
                }
                eventRepeatDaysView.setText(repeatDaysText);
            }


        }
        else{
            eventDateTimeText = dateToString(event.getEventDate())
                    +" at "
                    + event.getEventTime();
            eventRepeatDaysView.setVisibility(View.GONE);
        }


        eventDateTime.setText(eventDateTimeText);

        //POSTER IMAGE
        ImageView posterImage = view.findViewById(R.id.event_poster);
        if (event.getEventPoster() == null){
            posterImage.setVisibility(View.GONE);
        } else {
            Bitmap eventPoster = event.generatePoster();
            posterImage.setImageBitmap(eventPoster);
        }

        //EVENT DETAILS
        TextView eventDetails = view.findViewById(R.id.event_details);
        if (event.getEventDetails() != null){
            eventDetails.setText(event.getEventDetails());
        } else {
            eventDetails.setVisibility(View.GONE);
        }

        //WAITLIST SIZE TEXT
        TextView waitingList = view.findViewById(R.id.event_waiting_list);
        String waitlistText;
        if (event.getMaxEntrants() == -1){ //if there is no entrant cap
            waitlistText = event.getWaitingListSize() + " Entrants";
            waitingList.setText(waitlistText);
        } else { //if there is an entrant cap
            waitlistText = "Waiting List: " + event.getEntrantsList().size() + "/" + event.getMaxEntrants();
            waitingList.setText(waitlistText);
        }

        //EVENT STATUS
        TextView currentStatus = view.findViewById(R.id.event_status);
        if (event.getEventOver() == Boolean.TRUE){
            currentStatus.setText("Current Status: Not Accepting Entries");
        } else {
            currentStatus.setText("Current Status: Accepting Entries");
        }

        //check if the event requires geolocation
        TextView geoText = view.findViewById(R.id.geoText);
        if (geolocationRequired != Boolean.TRUE){
           confirmGeolocation.setVisibility(View.GONE);
           geoText.setVisibility(View.GONE);
        }

        //if the event is over, they cannot enter the event
        if (event.getEventOver() == Boolean.TRUE){
            enterEventSwitch.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        }

        //CHECKBOX TO ALLOW GEOLOCATION TO BE SHARED
        confirmGeolocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) { //the user wants notifications on
                    locationSharingAllowed = Boolean.TRUE;
                } else { //the user wants notifications off
                    locationSharingAllowed = Boolean.FALSE;
                }
            }
        });

        // SWITCH TO ENTER EVENT, DOES NOT SAVE UNTIL THE USER HITS SAVE THOUGH
        enterEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //the user wants to enter the event
                if (isChecked) {
                    // if geolocation required by the organizer
                    if (geolocationRequired == Boolean.TRUE) {
                        // if entrant has not allowed geolocation to be shared, show toast
                        if (locationSharingAllowed == Boolean.FALSE){
                            Toast.makeText(main, "You can only enter this event if you allow sharing of your geolocation", Toast.LENGTH_SHORT).show();
                            enterEventSwitch.setChecked(false);
                            // user has accepted to share geolocation, is approved to enter the event
                        } else if (locationSharingAllowed == Boolean.TRUE) {
                            userCanEnter = Boolean.TRUE;
                            saveUserLocation();
                        }
                        //if geolocation is not required, they can just enter the event
                    } else {
                        userCanEnter = Boolean.TRUE;
                    }

                }
            }
        });

        saveButton.setOnClickListener(v -> saveEvent(v));

        return view;
    }

    // Get the current location
    private void saveUserLocation() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Handle the location result
                            if (location != null) {
                                saveLocationToFirebase(location);
                            } else {
                                // Location is null, handle the case when location is not available
                                Toast.makeText(getContext(), "Unable to retrieve location.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // If permissions are not granted, request them
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Save location to Firebase
    private void saveLocationToFirebase(Location geoLocation) {
        UserDB userDB = connection.getUserDB();
        // Update location
        user.setGeoLocation(geoLocation);
        userDB.updateUserDocument(user);
    }

    /**
     * Author: Sam Lee
     * Edited: Erin-Marie
     * Add userRef to event's entrants list and update database.
     * [US 01.01.01] As an entrant, I want to join the waiting list for a specific event
     */
    private void saveEvent(View v) {


        //Check if the user can enter the event
        if (userCanEnter != Boolean.TRUE){
            Toast.makeText(getContext(), "Your are unable to enter this event", Toast.LENGTH_LONG).show();
        } else {

            // Add the entrant to the event in the database
            EventDB eventDB = connection.getEventDB();
            eventDB.addEntrant(event);

            Navigation.findNavController(v).navigate(R.id.nav_registered);

            // Update registered events list
            RegisteredEventFragment fragment = (RegisteredEventFragment) getParentFragmentManager().findFragmentById(R.id.nav_registered); // Use getParentFragmentManager() instead
            if (fragment != null) {
                RegisteredEventArrayAdapter adapter = fragment.getRegisteredEventArrayAdapter();
                if (adapter != null) {
                    adapter.add(event);  // Add the event to the adapter
                    adapter.notifyDataSetChanged();  // Notify the adapter about the change

                }
            }
        }

    }
    public String dateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
        String dateString = dateFormat.format(date);
        return dateString;
    }
}
