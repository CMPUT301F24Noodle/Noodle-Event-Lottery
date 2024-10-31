package com.example.myapplication.ui.home;

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
import java.util.List;

public class MyEventsListArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public MyEventsListArrayAdapter(@NonNull Context context, @NonNull List<Event> items) {
        super(context, 0, items);
        this.events = new ArrayList<>(events);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.scanned_list_items, parent, false);
        }

        // Lookup view for data population
        Event event = events.get(position);
        TextView eventName = convertView.findViewById(R.id.event_title);
        TextView eventDate = convertView.findViewById(R.id.event_date);
        TextView eventTime = convertView.findViewById(R.id.event_time);
        TextView orgName = convertView.findViewById(R.id.organizer_name);

        // Populate the data into the template view using the data object
        eventName.setText(event.getEventName());
        eventDate.setText(event.getEventDate() != null ? event.getEventDate().toString() : "No Date");
        eventTime.setText(event.getEventTime());
        orgName.setText(event.getOrganizer() != null ? event.getEventDate().toString() : "No Org");

        return view;
    }
}