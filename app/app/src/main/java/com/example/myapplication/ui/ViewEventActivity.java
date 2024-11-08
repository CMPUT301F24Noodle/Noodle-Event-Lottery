package com.example.myapplication.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.ui.registeredevents.RegisteredEventArrayAdapter;
import com.example.myapplication.ui.registeredevents.RegisteredEventFragment;
import com.google.firebase.firestore.DocumentReference;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;

/**
 * Author: Sam Lee
 * Activity for viewing an event scanned from QR code.
 *[US 01.06.01] As an entrant I want to view event details within the app by scanning the promotional QR code
 */
public class ViewEventActivity extends AppCompatActivity {

    private Event event;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_user);

        event = (Event) getIntent().getSerializableExtra("event");

        TextView eventName = findViewById(R.id.event_name);
        TextView eventDateTime = findViewById(R.id.event_date_time);
        TextView eventLocation = findViewById(R.id.event_location);
        Button saveButton = findViewById(R.id.event_save_button);

        eventName.setText(event.getEventName());
        eventDateTime.setText(event.getEventDate().toString() + " " + event.getEventTime());
        // TODO: currently we are not setting the event location
        // eventLocation.setText(event.getEventlocation());

        saveButton.setOnClickListener(v -> saveEvent());
    }

    /**
     * Author: Sam Lee
     * Add userRef to event's entrants list and update database.
     * [US 01.01.01] As an entrant, I want to join the waiting list for a specific event
     */
    private void saveEvent() {
        // Add user to event's entrants list
        UserDB userDB = new UserDB(new DBConnection(getApplicationContext()));
        DocumentReference userRef = userDB.getUserDocumentReference();
        event.addEntrant(userRef);

        // Update event in database
        EventDB eventDB = new EventDB(new DBConnection(getApplicationContext()));
        eventDB.updateEvent(event);

        // Update registered events list
        RegisteredEventFragment fragment = (RegisteredEventFragment) getSupportFragmentManager().findFragmentById(R.id.nav_registered); // Replace with your fragment's container ID
        if (fragment != null) {
            RegisteredEventArrayAdapter adapter = fragment.getRegisteredEventArrayAdapter();
            if (adapter != null) {
                adapter.add(event);
                adapter.notifyDataSetChanged();

                // Navigate back to registered events fragment
                finish();
            }
        }
    }
}
