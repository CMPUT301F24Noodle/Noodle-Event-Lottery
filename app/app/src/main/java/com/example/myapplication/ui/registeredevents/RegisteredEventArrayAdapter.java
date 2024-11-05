package com.example.myapplication.ui.registeredevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.objects.eventClasses.Event;

import java.util.ArrayList;

/**
 * Author: Sam Lee
 * Adapter for displaying a list of registered events.
 */
public class RegisteredEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public RegisteredEventArrayAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
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
        eventTime.setText(event.getEventTime());
        orgName.setText(event.getOrganizer() != null ? event.getOrganizer().getName() : "No Org");

        return view;
    }
}

/**
 * THIS IS A PLACEHOLDER FOR THE OLD REGISTERED EVENT ADAPTER
 * I(sam) was supposed to work on this adapter but I worked on
 * MyEventsListArrayAdapter,
 * which is a adapter for HostedEvents for Organizers.
 */
// import android.content.Context;
// import android.view.LayoutInflater;
// import android.view.View;
// import android.view.ViewGroup;
// import android.widget.ArrayAdapter;
// import android.widget.TextView;

// import androidx.annotation.NonNull;
// import androidx.annotation.Nullable;

// import com.example.myapplication.R;

// import java.util.List;

// public class RegisteredEventArrayAdapter extends ArrayAdapter<RegisteredEvent> {

// public RegisteredEventArrayAdapter(@NonNull Context context, @NonNull
// List<RegisteredEvent> items) {
// super(context, 0, items);
// }

// @NonNull
// @Override
// public View getView(int position, @Nullable View convertView, @NonNull
// ViewGroup parent) {
// // Get the data item for this position
// RegisteredEvent listItem = getItem(position);

// // Check if an existing view is being reused, otherwise inflate the view
// if (convertView == null) {
// convertView =
// LayoutInflater.from(getContext()).inflate(R.layout.registered_event_items,
// parent, false);
// }

// // Lookup view for data population
// TextView headingView = convertView.findViewById(R.id.eventname_status);
// TextView subheading1View = convertView.findViewById(R.id.event_date);
// TextView subheading2View = convertView.findViewById(R.id.event_time);
// TextView subheading3View = convertView.findViewById(R.id.organizer_name);

// // Populate the data into the template view using the data object
// headingView.setText(listItem.getheadStatus());
// subheading1View.setText(listItem.getSubheading1());
// subheading2View.setText(listItem.getSubheading2());
// subheading3View.setText(listItem.getSubheading3());

// // Return the completed view to render on screen
// return convertView;
// }
// }
