package com.example.myapplication.ui.myevents;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class OrganizedEventArrayAdapter extends ArrayAdapter<Event> {


    public OrganizedEventArrayAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //View view = super.getView(position, convertView, parent);
        Event event = getItem(position);
       // View view = View.findViewById(R.id.created_event_list);

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


        EventDB eventDB = new DBConnection(getContext()).getEventDB();
        // Set OnClickListener for the item
        convertView.setOnClickListener(v -> {
            Bundle manageArgs = new Bundle();
            manageArgs.putSerializable("eventDB", eventDB);
            openManageEventFragment(manageArgs);
        });

        return convertView;
    }


    /**
     * Opens the AddEventsFragment, allowing the user to add a new event.
     */
    private void openManageEventFragment(Bundle manageArgs) {
        // Create a new instance of AddEventsFragment
        ManageEventFragment addManageEventFragment = new ManageEventFragment();
        addManageEventFragment.setArguments(manageArgs);

//        // Begin the Fragment transaction
//        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.nav_host_fragment_content_main, addManageEventFragment); // Ensure this ID matches your main container ID
//        transaction.addToBackStack(null); // Adds the transaction to the back stack
//        transaction.commit();
    }

}
