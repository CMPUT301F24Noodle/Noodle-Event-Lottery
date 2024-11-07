//package com.example.myapplication.objects.addevents;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.example.myapplication.R;
//import com.example.myapplication.database.DBConnection;
//import com.example.myapplication.database.EventDB;
//import com.example.myapplication.objects.eventClasses.Event;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.zxing.WriterException;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class AddEvents extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private static final int PERMISSION_REQUEST_CODE = 100;
//
//    // Declare UI elements
//    private EditText eventNameEditText, eventLocationEditText, dateDayEditText, dateMonthEditText, dateYearEditText;
//    private EditText eventDetailsEditText, contactNumberEditText, maxParticipantsEditText, waitingListLimitEditText;
//    private Button addPosterButton, saveButton;
//    private TextView currentStatusTextView, removeActionTextView;
//    private ImageView posterImageView;
//    private Uri selectedImageUri;
//
//    private FirebaseFirestore db;
//    private EventDB eventDB;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.org_create_new_event);
//
//        // Initialize Firebase Firestore and EventDB
//        db = FirebaseFirestore.getInstance();
//        DBConnection connection = new DBConnection(this);  // Passing context
//        eventDB = new EventDB(connection);
//
//        // Initialize the UI elements
//        initializeViews();
//
//        // Set up button listeners
//        setButtonListeners();
//    }
//
//    // Method to initialize all the views
//    private void initializeViews() {
//        eventNameEditText = findViewById(R.id.event_name_edit);
//        eventLocationEditText = findViewById(R.id.event_location_edit);
//        dateDayEditText = findViewById(R.id.date_picker_DD);
//        dateMonthEditText = findViewById(R.id.date_picker_MM);
//        dateYearEditText = findViewById(R.id.date_picker_YY);
//        eventDetailsEditText = findViewById(R.id.textbox_detail);
//        contactNumberEditText = findViewById(R.id.contact_num);
//        maxParticipantsEditText = findViewById(R.id.max_participants);
//        waitingListLimitEditText = findViewById(R.id.waiting_list_limit);
//
//        addPosterButton = findViewById(R.id.add_poster_button);
//        saveButton = findViewById(R.id.save_button);
//        currentStatusTextView = findViewById(R.id.current_status);
//        removeActionTextView = findViewById(R.id.remove_action);
//        posterImageView = findViewById(R.id.poster_image_view);
//    }
//
//    // Method to set up button click listeners
//    private void setButtonListeners() {
//        // Add poster button click listener
//        addPosterButton.setOnClickListener(v -> {
//            if (checkAndRequestPermissions()) {
//                openImagePicker();
//            }
//        });
//
//        // Remove poster click listener
//        removeActionTextView.setOnClickListener(v -> {
//            posterImageView.setImageResource(0);
//            selectedImageUri = null;
//            currentStatusTextView.setText("Current: None");
//            Toast.makeText(AddEvents.this, "Poster Removed", Toast.LENGTH_SHORT).show();
//        });
//
//        // Save button click listener
//        saveButton.setOnClickListener(v -> saveEventDetails());
//    }
//
//    // Method to save the event details
//    private void saveEventDetails() {
//        String eventName = eventNameEditText.getText().toString().trim();
//        String eventLocation = eventLocationEditText.getText().toString().trim();
//        String eventDay = dateDayEditText.getText().toString().trim();
//        String eventMonth = dateMonthEditText.getText().toString().trim();
//        String eventYear = dateYearEditText.getText().toString().trim();
//        String eventDetails = eventDetailsEditText.getText().toString().trim();
//        String contactNumber = contactNumberEditText.getText().toString().trim();
//        String maxParticipants = maxParticipantsEditText.getText().toString().trim();
//        String waitingListLimit = waitingListLimitEditText.getText().toString().trim();
//
//        if (eventName.isEmpty() || eventLocation.isEmpty() || eventDay.isEmpty() ||
//                eventMonth.isEmpty() || eventYear.isEmpty() || maxParticipants.isEmpty()) {
//            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        MyEvent event = new MyEvent();
//        event.setEventName(eventName);
//        event.setEventLocation(eventLocation);
//        event.setEventDate(eventDay + "-" + eventMonth + "-" + eventYear);
//        event.setEventDetails(eventDetails);
//        event.setContactNumber(contactNumber);
//        event.setMaxParticipants(maxParticipants);
//        event.setWaitingListLimit(waitingListLimit);
//
//        if (selectedImageUri != null) {
//            event.setPosterUri(selectedImageUri.toString());
//        }
//
//
//        if (selectedImageUri != null) {
//            event.setPosterUri(selectedImageUri.toString());
//        }
//
//        // Add event to Firestore using EventDB
//        eventDB.addEvent(event);
//
//        Toast.makeText(this, "Event saved successfully!", Toast.LENGTH_SHORT).show();
//        clearFields();
//    }
//
//    // Method to clear all input fields
//    private void clearFields() {
//        eventNameEditText.setText("");
//        eventLocationEditText.setText("");
//        dateDayEditText.setText("");
//        dateMonthEditText.setText("");
//        dateYearEditText.setText("");
//        eventDetailsEditText.setText("");
//        contactNumberEditText.setText("");
//        maxParticipantsEditText.setText("");
//        waitingListLimitEditText.setText("");
//        currentStatusTextView.setText("Current: None");
//        posterImageView.setImageResource(0);
//    }
//
//    // Open image picker to select a photo
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    // Handle the result from the image picker
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
//            selectedImageUri = data.getData();
//            displaySelectedImage(selectedImageUri);
//        }
//    }
//
//    // Helper method to display the selected image
//    private void displaySelectedImage(Uri imageUri) {
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//            posterImageView.setImageBitmap(bitmap);
//            currentStatusTextView.setText("Current: Poster Added");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Check and request permissions for accessing the gallery
//    private boolean checkAndRequestPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//            return false;
//        }
//        return true;
//    }
//
//    // Handle the result of permission requests
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            openImagePicker();
//        } else {
//            Toast.makeText(this, "Permission is required to add a poster", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
