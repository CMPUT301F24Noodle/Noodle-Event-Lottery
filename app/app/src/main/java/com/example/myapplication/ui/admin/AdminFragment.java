package com.example.myapplication.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;

public class AdminFragment extends Fragment {

    public MainActivity main;
    public DBConnection connection;
    public UserDB userDB; // userDB instance for the current user
    public EventDB eventDB;
    public NotificationDB notifDB;
    public String uuid;
    public UserProfile user;
    public Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment_admin layout
        View rootView = null;

        getVarFromMain();
        if ( user.getAdmin() != Boolean.FALSE){
            rootView = inflater.inflate(R.layout.fragment_admin, container, false);
            // Initialize the buttons
            Button profileButton = rootView.findViewById(R.id.nav_admin_profile);
            Button eventButton = rootView.findViewById(R.id.nav_admin_event);
            Button facilityButton = rootView.findViewById(R.id.nav_admin_facility);
            Button imageButton = rootView.findViewById(R.id.nav_admin_img);

            // Set click listeners for each button
            profileButton.setOnClickListener(v -> {
                // Navigate to AdminProfileFragment using NavController
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.adminProfileFragment);

                Toast.makeText(getContext(), "Navigating to Admin Profile", Toast.LENGTH_SHORT).show();
            });


            eventButton.setOnClickListener(v -> {
                // Handle Event button click
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.adminEventFragment);
                Toast.makeText(getContext(), "Event button clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Event Fragment or Activity
            });

            facilityButton.setOnClickListener(v -> {
                // Handle QR Code button click
                Toast.makeText(getContext(), "QR Code button clicked", Toast.LENGTH_SHORT).show();
                // Perform action for QR Code
            });

            imageButton.setOnClickListener(v -> {
                // Handle Image button click
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.adminPhotoFragment);

                Toast.makeText(getContext(), "Image button clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Image Fragment or Activity
            });

        } else {
            Toast.makeText(getContext(), "You are not an Admin", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    /**
     * Author: Erin-Marie
     * Gets some of the variables from MainActivity that we will need
     */
    public void getVarFromMain() {
        main = (MainActivity) getActivity();
        assert main != null;
        connection = main.connection;

        notifDB = connection.getNotifDB();
        user = connection.getUser();
        eventDB = connection.getEventDB();
        userDB = connection.getUserDB();
    }
}
