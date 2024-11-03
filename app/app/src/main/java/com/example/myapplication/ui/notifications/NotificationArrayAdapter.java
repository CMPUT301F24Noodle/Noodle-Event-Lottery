package com.example.myapplication.ui.notifications;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.ui.registeredevents.RegisteredEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Erin-Marie
 * custom array adapter for the arraylist of notifications, to be displayed in the notification fragment
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {

    public NotificationArrayAdapter(@NonNull Context context, @NonNull ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }

    /**
     * Author: Erin-Marie
     * I don't want the notifications to be selectable so this makes them not selectable
     * @param position the position of the notification
     * @return false so they can't be selected
     * Reference: <a href="https://stackoverflow.com/questions/5126581/can-i-make-a-listview-item-not-selectable">...</a>
     */
    @Override
    public boolean isEnabled(int position) {
        return false;
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
       Notification notif = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        // Lookup view for data population
        TextView headingView = convertView.findViewById(R.id.message_title);
        TextView subheading1View = convertView.findViewById(R.id.message_sender);
        TextView subheading2View = convertView.findViewById(R.id.message_body);


        // Populate the data into the template view using the data object
        headingView.setText(notif.getTitle());
        subheading1View.setText(notif.getSender());
        subheading2View.setText(notif.getMessage());


        // Return the completed view to render on screen
        return convertView;
    }
}
