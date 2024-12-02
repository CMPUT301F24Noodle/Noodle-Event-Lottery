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
import com.example.myapplication.objects.UserProfile;

import java.util.ArrayList;
import java.util.Objects;

public class AdminProfileArrayAdapter extends ArrayAdapter<UserProfile> {


    public AdminProfileArrayAdapter(@NonNull Context context, @NonNull ArrayList<UserProfile> events) {
        super(context, 0, events);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        UserProfile userProfile = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_profile, parent, false);


        // Lookup view for data population
        TextView headingView = convertView.findViewById(R.id.profile_name);
        TextView subheading1View = convertView.findViewById(R.id.email);
        TextView subheading2View = convertView.findViewById(R.id.roles);
        TextView subheading3View = convertView.findViewById(R.id.profileid);


        // Populate the data into the template view using the data object
        assert userProfile != null;
        if (userProfile.getName() != null) {
            headingView.setText(userProfile.getName());
        } else {
            headingView.setText("Anon_User");
        }
        if (userProfile.getEmail() != null) {
            subheading1View.setText(userProfile.getEmail());
        }
        subheading2View.setText(Objects.requireNonNull(userProfile.getUuid()));


        // Return the completed view to render on screen
        return convertView;
    }
}
