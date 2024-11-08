package com.example.myapplication.ui.myevents;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
        assert main != null;

        // Set up DBConnection and EventDB from MainActivity
        this.connection = main.connection;
        this.eventDB = main.eventDB;
        this.currentUserProfile = main.user; // Ensure this is initialized in MainActivity

        initializeViews(view);
        setButtonListeners(view);

        return view;
    }

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

        //posterImageView = view.findViewById(R.id.poster_image_view);
    }

    private void setButtonListeners(View view) {
        addPosterButton.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                openImagePicker();
            }
        });

        removeActionTextView.setOnClickListener(v -> {
            posterImageView.setImageResource(0);
            selectedImageUri = null;
            currentStatusTextView.setText("Current: None");
            Toast.makeText(getContext(), "Poster Removed", Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> saveEventDetails());
    }

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
        if (event.getEventID() != null) { // Assuming a non-null ID indicates success
            Toast.makeText(getContext(), "Event saved successfully!", Toast.LENGTH_SHORT).show();

            clearFields(); // Clear fields after saving

            // Prepare date formatting

        } else {
            Toast.makeText(getContext(), "Failed to save event. Please try again.", Toast.LENGTH_SHORT).show();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(event.getEventDate());

            // Logging to confirm the data
            Log.d("AddEventFragment", "Event Name: " + eventName);
            Log.d("AddEventFragment", "Event Location: " + eventLocation);
            Log.d("AddEventFragment", "Event Date: " + formattedDate);
            Log.d("AddEventFragment", "Event Details: " + event.getEventDetails());
            Log.d("AddEventFragment", "Event Waiting List: 33/45"); // Placeholder for waiting list count

            // Start EditEventActivity with event details
            Intent intent = new Intent(getContext(), EditEventActivity.class);
            intent.putExtra("event_id", event.getEventID());
            intent.putExtra("event_name", eventName);
            intent.putExtra("event_location", eventLocation);
            intent.putExtra("event_date_time", formattedDate); // Pass formatted date
            intent.putExtra("event_details", event.getEventDetails());
            intent.putExtra("event_waiting_list", "33/45"); // Example for waiting list count
            startActivity(intent);

        }
    }

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
        posterImageView.setImageResource(0);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

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

    private void displaySelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            posterImageView.setImageBitmap(bitmap);
            currentStatusTextView.setText("Current: Poster Added");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }
}
