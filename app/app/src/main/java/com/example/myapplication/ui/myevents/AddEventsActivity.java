package com.example.myapplication.ui.myevents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;

public class AddEventsActivity extends AppCompatActivity {
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
    private UserProfile currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_create_new_event);

        db = FirebaseFirestore.getInstance();
        DBConnection connection = new DBConnection(this);
        eventDB = new EventDB(connection);

        initializeViews();
        initializeCurrentUser();
        setButtonListeners();
    }

    private void initializeViews() {
        eventNameEditText = findViewById(R.id.event_name_edit);
        eventLocationEditText = findViewById(R.id.event_location_edit);
        dateDayEditText = findViewById(R.id.date_picker_DD);
        dateMonthEditText = findViewById(R.id.date_picker_MM);
        dateYearEditText = findViewById(R.id.date_picker_YY);
        eventDetailsEditText = findViewById(R.id.textbox_detail);
        contactNumberEditText = findViewById(R.id.contact_num);
        maxParticipantsEditText = findViewById(R.id.max_participants);
        waitingListLimitEditText = findViewById(R.id.waiting_list_limit);

        addPosterButton = findViewById(R.id.add_poster_button);
        saveButton = findViewById(R.id.save_button);
        currentStatusTextView = findViewById(R.id.current_status);
        removeActionTextView = findViewById(R.id.remove_action);
        posterImageView = findViewById(R.id.poster_image_view);
    }

    private void initializeCurrentUser() {
        // This should retrieve the actual user profile data in your app
        currentUserProfile = new UserProfile();
        currentUserProfile.setName("John Doe");
        currentUserProfile.setEmail("johndoe@example.com");
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
            Toast.makeText(this, "Poster Removed", Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> saveEventDetails());
    }

    private void saveEventDetails() {
        String eventName = eventNameEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        String maxParticipants = maxParticipantsEditText.getText().toString().trim();

        if (eventName.isEmpty() || eventLocation.isEmpty() || maxParticipants.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
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

        // Add event to Firebase using EventDB
        eventDB.addEvent(event);

        Toast.makeText(this, "Event saved successfully!", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            displaySelectedImage(selectedImageUri);
        }
    }

    private void displaySelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            posterImageView.setImageBitmap(bitmap);
            currentStatusTextView.setText("Current: Poster Added");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
            Toast.makeText(this, "Permission is required to add a poster", Toast.LENGTH_SHORT).show();
        }
    }
}
