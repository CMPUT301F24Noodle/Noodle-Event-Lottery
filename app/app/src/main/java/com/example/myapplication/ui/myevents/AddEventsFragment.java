package com.example.myapplication.ui.myevents;

import android.Manifest;
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

import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;

public class AddEventsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText eventNameEditText, eventLocationEditText, dateDayEditText, dateMonthEditText, dateYearEditText;
    private EditText eventDetailsEditText, contactNumberEditText, maxParticipantsEditText, waitingListLimitEditText;
    private Button addPosterButton, saveButton;
    private TextView currentStatusTextView, removeActionTextView;
    private ImageView posterImageView;
    private Uri selectedImageUri;

    private FirebaseFirestore db;
    private EventDB eventDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_create_new_event, container, false);

        // Initialize Firebase Firestore and EventDB
        db = FirebaseFirestore.getInstance();
        DBConnection connection = new DBConnection(requireContext());
        eventDB = new EventDB(connection);

        // Initialize the UI elements
        initializeViews(view);

        // Set up button listeners
        setButtonListeners();

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
        posterImageView = view.findViewById(R.id.poster_image_view);
    }

    private void setButtonListeners() {
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
        String eventDay = dateDayEditText.getText().toString().trim();
        String eventMonth = dateMonthEditText.getText().toString().trim();
        String eventYear = dateYearEditText.getText().toString().trim();
        String eventDetails = eventDetailsEditText.getText().toString().trim();
        String contactNumber = contactNumberEditText.getText().toString().trim();
        String maxParticipants = maxParticipantsEditText.getText().toString().trim();
        String waitingListLimit = waitingListLimitEditText.getText().toString().trim();

        if (eventName.isEmpty() || eventLocation.isEmpty() || eventDay.isEmpty() ||
                eventMonth.isEmpty() || eventYear.isEmpty() || maxParticipants.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize a new Event object
        Event event = new Event();
        event.setEventName(eventName);
        event.setEventDetails(eventDetails);
        event.setEventDate(new Date()); // Replace with actual date parsing if needed
        event.setContactNumber(contactNumber);
        event.setMaxEntrants(Integer.parseInt(maxParticipants));

        if (selectedImageUri != null) {
            event.setEventPoster(selectedImageUri.toString());
        }

        eventDB.addEvent(event);

        Toast.makeText(getContext(), "Event saved successfully!", Toast.LENGTH_SHORT).show();
        clearFields();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            displaySelectedImage(selectedImageUri);
        }
    }

    private void displaySelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            posterImageView.setImageBitmap(bitmap);
            currentStatusTextView.setText("Current: Poster Added");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            Toast.makeText(getContext(), "Permission is required to add a poster", Toast.LENGTH_SHORT).show();
        }
    }
}
