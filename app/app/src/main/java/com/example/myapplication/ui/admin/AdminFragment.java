/**
 * AdminFragment.java
 *
 * This class represents the AdminFragment which provides the user interface
 * for various administrative actions in the application. It inflates the
 * `fragment_admin` layout and sets up navigation for buttons that lead to
 * different admin-related fragments or activities.
 *
 * Author: Nishchay Ranjan
 */

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

import com.example.myapplication.R;

public class AdminFragment extends Fragment {

    /**
     * Called to create and return the view hierarchy associated with this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment_admin layout
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize the buttons
        Button profileButton = rootView.findViewById(R.id.nav_admin_profile);
        Button eventButton = rootView.findViewById(R.id.nav_admin_event);
        Button qrCodeButton = rootView.findViewById(R.id.nav_admin_qr);
        Button imageButton = rootView.findViewById(R.id.nav_admin_img);

        // Set click listeners for each button

        /**
         * Handles the Profile button click.
         * Navigates to the AdminProfileFragment and displays a toast message.
         */
        profileButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.adminProfileFragment);
            Toast.makeText(getContext(), "Navigating to Admin Profile", Toast.LENGTH_SHORT).show();
        });

        /**
         * Handles the Event button click.
         * Navigates to the AdminEventFragment and displays a toast message.
         */
        eventButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.adminEventFragment);
            Toast.makeText(getContext(), "Event button clicked", Toast.LENGTH_SHORT).show();
        });

        /**
         * Handles the QR Code button click.
         * Displays a toast message indicating the QR Code action.
         */
        qrCodeButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "QR Code button clicked", Toast.LENGTH_SHORT).show();
            // Add functionality for QR Code here
        });

        /**
         * Handles the Image button click.
         * Navigates to the AdminPhotoFragment and displays a toast message.
         */
        imageButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.adminPhotoFragment);
            Toast.makeText(getContext(), "Image button clicked", Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }
}
