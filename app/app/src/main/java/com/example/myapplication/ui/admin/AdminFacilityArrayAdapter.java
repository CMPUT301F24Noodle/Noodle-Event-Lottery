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
import com.example.myapplication.objects.Facility;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Array Adapter for the Admin viewing facilities fragment
 */

public class AdminFacilityArrayAdapter extends ArrayAdapter<Facility> {


    public AdminFacilityArrayAdapter(@NonNull Context context, int resource, ArrayList<Facility> facilities) {
        super(context, resource, facilities);
    }

    /**
     * Author: Erin-Marie
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
        Facility facility = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_event, parent, false);


        // Lookup view for data population
        TextView headingView = convertView.findViewById(R.id.event_title);
        TextView subheading1View = convertView.findViewById(R.id.organizer_name);
        TextView subheading2View = convertView.findViewById(R.id.event_date);


        // Populate the data into the template view using the data object
        assert facility != null;
        headingView.setText(facility.getFacilityName());

        if (facility.getLocation() != null){
            subheading1View.setText(facility.getLocation());
        } else {
            subheading1View.setText("Remote Location");
        }

        if(facility.getOwner() != null) {
            subheading2View.setText(facility.getOwner().getName());
        } else {
            subheading2View.setText("Anon_Owner");
        }



        // Return the completed view to render on screen
        return convertView;
    }
}


