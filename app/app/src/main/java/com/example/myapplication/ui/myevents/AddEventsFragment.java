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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

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
    private Uri selectedImageUri;

    private DBConnection connection;
    private EventDB eventDB;
    private UserProfile currentUserProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_create_new_event, container, false);

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            this.connection = main.connection;
            this.eventDB = main.eventDB;
            this.currentUserProfile = main.user;
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

        addPosterButton = view.findViewById(R.id.add_poster_button);
        saveButton = view.findViewById(R.id.save_button);
        currentStatusTextView = view.findViewById(R.id.current_status);
        removeActionTextView = view.findViewById(R.id.remove_action);
    }

    /**
     * Sets button click listeners for adding/removing poster and saving event details.
     */
    private void setButtonListeners() {
        addPosterButton.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                openImagePicker();
            }
        });

        removeActionTextView.setOnClickListener(v -> {
            if (posterImageView != null) {
                posterImageView.setImageResource(0);
                selectedImageUri = null;
                currentStatusTextView.setText("Current: None");
                Toast.makeText(getContext(), "Poster Removed", Toast.LENGTH_SHORT).show();
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
        String maxParticipants = maxParticipantsEditText.getText().toString().trim();

        if (eventName.isEmpty() || eventLocation.isEmpty() || maxParticipants.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event();
        event.setEventName(eventName);
        event.setEventDetails(eventDetailsEditText.getText().toString().trim());
        event.setMaxEntrants(Integer.parseInt(maxParticipants));
        event.setEventDate(new Date());
        event.setContact(contactNumberEditText.getText().toString().trim());

        if (selectedImageUri != null) {
            event.setEventPoster(selectedImageUri.toString());
        }

        event.setOrganizer(currentUserProfile);

        eventDB.addEvent(event);
        if (event.getEventID() != null) {
            Toast.makeText(getContext(), "Event saved successfully!", Toast.LENGTH_SHORT).show();
            clearFields();

            // Prepare date formatting for passing to EditEventFragment

        } else {
            Toast.makeText(getContext(), "Failed to save event. Please try again.", Toast.LENGTH_SHORT).show();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(event.getEventDate());

            // Create arguments to pass to EditEventFragment
            Bundle args = new Bundle();
            args.putString("event_id", event.getEventID());
            args.putString("event_name", eventName);
            args.putString("event_location", eventLocation);
            args.putString("event_date_time", formattedDate);
            args.putString("event_details", event.getEventDetails());
            args.putString("event_waiting_list", "33/45");

            EditEventFragment editEventFragment = new EditEventFragment();
            editEventFragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, editEventFragment)
                    .addToBackStack(null)
                    .commit();
        }
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
     * Opens the image picker to select an event poster.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Checks if the storage permission is granted; requests it if not.
     * @return true if permission is granted, false otherwise.
     */
    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            displaySelectedImage(selectedImageUri);
        }
    }

    /**
     * Displays the selected image in the ImageView and updates status text.
     * @param imageUri the URI of the selected image.
     */
    private void displaySelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            if (posterImageView != null) {
                posterImageView.setImageBitmap(bitmap);
                currentStatusTextView.setText("Current: Poster Added");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }
}