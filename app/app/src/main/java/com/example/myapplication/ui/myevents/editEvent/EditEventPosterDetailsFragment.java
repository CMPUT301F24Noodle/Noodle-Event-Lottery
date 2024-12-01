package com.example.myapplication.ui.myevents.editEvent;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
    private EditText eventDetailsEditText, contactNumberEditText, maxParticipantsEditText, waitingListLimitEditText, eventTimeHour, eventTimeMinute;
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
        LinearLayout geolocationLayout = view.findViewById(R.id.geolocationLayout);

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
        geolocationLayout.setVisibility(View.GONE);



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

        saveButton.setOnClickListener(v -> saveEventDetails(v));
    }


    /**
     * Saves the event description, and returns to EditEventFragment
     */
    private void saveEventDetails(View v) {
        String newEventDetails = eventDetailsEditText.getText().toString();
        event.setEventDetails(newEventDetails);
        eventDB.updateEvent(event);

        // return to back to EditEventFragment
        NavController navController = Navigation.findNavController(v);
        navController.popBackStack();
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

