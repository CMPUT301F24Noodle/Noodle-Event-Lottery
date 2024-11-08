package com.example.myapplication.ui.myevents;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.objects.eventClasses.Event;

import java.util.List;

public class OrganizedEventArrayAdapter extends ArrayAdapter<Event> {

    public OrganizedEventArrayAdapter(@NonNull Context context, @NonNull List<Event> events) {
        super(context, android.R.layout.simple_list_item_1, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Use the built-in simple_list_item_1 layout to display the event name
        TextView textView = (TextView) super.getView(position, convertView, parent);

        Event event = getItem(position);
        if (event != null) {
            textView.setText(event.getEventName());  // Display the event name
        }

        return textView;
    }
}
