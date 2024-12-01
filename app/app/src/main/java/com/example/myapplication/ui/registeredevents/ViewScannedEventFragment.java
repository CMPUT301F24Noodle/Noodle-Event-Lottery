package com.example.myapplication.ui.registeredevents;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.google.firebase.firestore.DocumentReference;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;

/**
 * Author: Sam Lee
 * Activity for viewing an event scanned from QR code.
 *[US 01.06.01] As an entrant I want to view event details within the app by scanning the promotional QR code
 */
public class ViewScannedEventFragment extends Fragment {

    private Event event;
    private DBConnection connection;


    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        assert inflater != null;
        View view = inflater.inflate(R.layout.view_event_user, container, false);

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            this.connection = main.connection;
            this.event = main.scannedEvent;
        }

        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventDateTime = view.findViewById(R.id.event_date_time);
        TextView eventLocation = view.findViewById(R.id.event_location);
        Button saveButton = view.findViewById(R.id.event_save_button);
        ImageView posterImage = view.findViewById(R.id.event_poster);

        // Display the poster
        if (event.getEventPoster() != null) {
            Bitmap eventPoster = event.generatePoster();
            posterImage.setImageBitmap(eventPoster);
        }

        // Set event details
        eventName.setText(event.getEventName());
        eventDateTime.setText(event.getEventDate().toString() + " " + event.getEventTime());

        // TODO: currently we are not setting the event location
        // eventLocation.setText(event.getEventlocation());

        saveButton.setOnClickListener(v -> saveEvent(v));

        return view;
    }

    /**
     * Author: Sam Lee
     * Add userRef to event's entrants list and update database.
     * [US 01.01.01] As an entrant, I want to join the waiting list for a specific event
     * TODO: addEntrant(event) returns False if the user could not be added, whether because the waitlist is full, or if the event has already ended.
     *      should print a toast or something if the user could not be added
     */
    private void saveEvent(View v) {
        // Add user to event's entrants list
        UserDB userDB = new UserDB(connection);
        DocumentReference userRef = userDB.getUserDocumentReference();
        // TODO USERS DONT GET UPDATED?


        // Update event in database
        EventDB eventDB = new EventDB(connection);
        eventDB.addEntrant(event);
        eventDB.updateEvent(event);

        // Update registered events list
        RegisteredEventFragment fragment = (RegisteredEventFragment) getParentFragmentManager().findFragmentById(R.id.nav_registered); // Use getParentFragmentManager() instead
        if (fragment != null) {
            RegisteredEventArrayAdapter adapter = fragment.getRegisteredEventArrayAdapter();
            if (adapter != null) {
                adapter.add(event);  // Add the event to the adapter
                adapter.notifyDataSetChanged();  // Notify the adapter about the change

                // now navigate back to registered events home page
                //Navigation.findNavController(v).popBackStack();
                //Navigation.findNavController(v).popBackStack();
                //Navigation.findNavController(v).navigate(R.id.nav_registered);

            }
        }
        // now navigate back to registered events home page
        //Navigation.findNavController(v).popBackStack();
        //Navigation.findNavController(v).popBackStack();
        Navigation.findNavController(v).navigate(R.id.nav_registered);
    }
}
