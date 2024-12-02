package com.example.myapplication.ui.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;

import java.util.ArrayList;
import java.util.Objects;

public class AdminEventArrayAdapter extends ArrayAdapter<Event>{


    public AdminEventArrayAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, 0, events);
    }


    /**
     * Author: Erin-Marie
     * getView for the Notification Fragment, gets the Notification view for thr notifiation at position
     * @param position the int position of the notification being viewed
     * @param convertView this is important idk why
     * @param parent the parent view
     * @return view to be used for that notification
     */
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_event, parent, false);


        // Lookup view for data population
        TextView headingView = convertView.findViewById(R.id.event_title);
        TextView subheading1View = convertView.findViewById(R.id.organizer_name);
        TextView subheading2View = convertView.findViewById(R.id.event_date);


        // Populate the data into the template view using the data object
        assert event != null;
        headingView.setText(event.getEventName());
        assert event.getOrganizer() != null;
        subheading1View.setText(event.getOrganizer().getName());

        subheading2View.setText(Objects.requireNonNull(event.getEventDate()).toString());


        // Return the completed view to render on screen
        return convertView;
    }
}
