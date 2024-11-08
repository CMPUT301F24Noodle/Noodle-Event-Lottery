package com.example.myapplication.ui.myevents;

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
import com.example.myapplication.objects.userProfileClasses.UserProfile;

import java.util.List;

public class OrganizedEventArrayAdapter extends ArrayAdapter<Event> {

    public OrganizedEventArrayAdapter(@NonNull Context context, @NonNull List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.registered_event_items, parent, false);
        }

        TextView eventName = convertView.findViewById(R.id.eventname_status);
        TextView organizerName = convertView.findViewById(R.id.organizer_name);

        if (event != null) {
            eventName.setText(event.getEventName());  // Display the event name
            UserProfile organizer = event.getOrganizer();
            assert organizer != null;
            organizerName.setText(organizer.getName());
        }

        return convertView;
    }
}
