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
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.ui.notifications.Notification;

import java.util.ArrayList;

public class EntrantArrayAdapter extends ArrayAdapter<UserProfile> {

    public EntrantArrayAdapter(@NonNull Context context, @NonNull ArrayList<UserProfile> entrants) {
        super(context, 0, entrants);
    }


    /**
     * Author: Erin-Marie
     * getView for the Notification Fragment, gets the Notification view for thr notifiation at position
     * @param position the int position of the notification being viewed
     * @param convertView this is important idk why
     * @param parent the parent view
     * @return view to be used for that notification
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        UserProfile user = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_item, parent, false);
        }

        // Lookup view for data population
        TextView headingView = convertView.findViewById(R.id.user_name);



        // Populate the data into the template view using the data object
        assert user != null;
        headingView.setText(user.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
