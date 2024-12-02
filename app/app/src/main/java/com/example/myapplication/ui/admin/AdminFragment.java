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

/**
 * AdminFragment
 *
 * This fragment represents the administrative interface for the application.
 * It provides navigation to different administrative functionalities, such as
 * managing user profiles, events, facilities, and images. Access to this fragment
 * is restricted to admin users, and a toast message is displayed if a non-admin user
 * attempts to access it.
 *
 * Author: Nishchay Ranjan
 */
public class AdminFragment extends Fragment {

    public MainActivity main; // Reference to the MainActivity
    public DBConnection connection; // Database connection object
    public UserDB userDB; // User database instance
    public EventDB eventDB; // Event database instance
    public NotificationDB notifDB; // Notification database instance
    public String uuid; // UUID of the user
    public UserProfile user; // Current user's profile
    public Event event; // Current event object

    /**
     * onCreateView
     *
     * This method inflates the admin fragment layout and initializes its components.
     * It sets up navigation for buttons to various admin features and ensures that
     * only admin users can access the interface. Non-admin users receive a toast notification.
     *
     * @param inflater LayoutInflater used to inflate the fragment layout
     * @param container Optional parent view
     * @param savedInstanceState Bundle containing saved state
     * @return View representing the admin interface or null if user is not an admin
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment_admin layout
        View rootView = null;

        // Retrieve variables from MainActivity
        getVarFromMain();

        // Check if the user is an admin
        if (user.getAdmin() != Boolean.FALSE) {
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
            });

            eventButton.setOnClickListener(v -> {
                // Navigate to AdminEventFragment using NavController
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.adminEventFragment);
            });

            facilityButton.setOnClickListener(v -> {
                // Navigate to AdminFacilityFragment using NavController
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.adminFacilityFragment);
            });

            imageButton.setOnClickListener(v -> {
                // Navigate to AdminPhotoFragment using NavController
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.adminPhotoFragment);
            });

        } else {
            // Display a toast if the user is not an admin
            Toast.makeText(getContext(), "You are not an Admin", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    /**
     * getVarFromMain
     *
     * This method retrieves necessary variables from MainActivity, including
     * database connections and the current user's profile. These variables are
     * essential for the functionality of the admin fragment.
     *
     * Author: Erin Marie
     */
    public void getVarFromMain() {
        main = (MainActivity) getActivity();
        assert main != null;

        // Retrieve database connections and user details from MainActivity
        connection = main.connection;
        notifDB = connection.getNotifDB();
        user = connection.getUser();
        eventDB = connection.getEventDB();
        userDB = connection.getUserDB();
    }
}
