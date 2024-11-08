package com.example.myapplication.ui.registeredevents;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.database.EventDB;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

/**
 * Author: Sam Lee
 * Adapter for displaying a list of registered events.
 */
public class RegisteredEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    private UserDB userDB;
    private EventDB eventDB;
    private DocumentReference currUser;

    public RegisteredEventArrayAdapter(@NonNull Context context, @NonNull ArrayList<Event> events, @NonNull UserDB userDB, @NonNull EventDB eventDB) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
        this.userDB = userDB;
        this.eventDB = eventDB;
        this.currUser = userDB.getCurrentUser().getDocRef();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.registered_event_items, parent, false);
        }

        // Lookup view for data population
        Event event = events.get(position);
        TextView eventName = view.findViewById(R.id.eventname_status);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView orgName = view.findViewById(R.id.organizer_name);

        // Populate the data into the template view using the data object
        eventName.setText(event.getEventName());
        eventDate.setText(event.getEventDate() != null ? event.getEventDate().toString() : "No Date");
        eventTime.setText(event.getEventTime() != null ? event.getEventTime() : "No Time");
        orgName.setText(event.getOrganizer() != null ? event.getOrganizer().getName() : "No Org");

        // Set OnClickListener for the item
        view.setOnClickListener(v -> {
            if (event.hasAccepted(event, currUser)) {
                showAcceptDeclineDialog(event);
            } else {
                Toast.makeText(context, "You have not won the lottery for this event.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Authot: Sam Lee
     * Show a dialog to accept or decline the invitation.
     * 
     * @param event
     */
    private void showAcceptDeclineDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Event Invitation")
                .setMessage("You have won the lottery for this event. Do you want to accept or decline the invitation?")
                .setPositiveButton("Accept", (dialog, which) -> {
                    acceptInvitation(event);
                })
                .setNegativeButton("Decline", (dialog, which) -> {
                    declineInvitation(event);
                })
                .show();
    }

    /**
     * Author: Sam Lee
     * Accept the invitation to the event.
     * 
     * @param event
     */
    public void acceptInvitation(Event event) {
        event.getAcceptedList().add(currUser);
        event.getWinnersList().remove(currUser);
        event.getEntrantsList().remove(currUser);
        eventDB.updateEvent(event);
        Toast.makeText(context, "You have accepted the invitation.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Author: Sam Lee
     * Decline the invitation to the event.
     * 
     * @param event
     */
    public void declineInvitation(Event event) {
        event.getDeclinedList().add(currUser);
        event.getEntrantsList().remove(currUser);
        event.getWinnersList().remove(currUser);
        eventDB.updateEvent(event);
        Toast.makeText(context, "You have declined the invitation.", Toast.LENGTH_SHORT).show();
    }
}
