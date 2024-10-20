package com.example.myapplication.ui.user_profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

// TODO add a class header
// TODO add all strings here and in xml to string resource file
// TODO set up a ping system to let the activity know when the fragment has closed
// TODO add more facility attributes?
public class EditFacilityFragment extends DialogFragment {
    private Facility facility;
    private UserProfile user;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_create_facility, null);

        // get the user
        user = new UserProfile(); // TODO get the existing profile from database using device ID
        facility = user.getFacility();

        // get all the text views
        EditText EditFacilityName = view.findViewById(R.id.edit_text_facility_name);
        EditText EditFacilityLocation = view.findViewById(R.id.edit_text_facility_location);




        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Facility")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Create", (dialog, which) -> {
                    String facilityName = EditFacilityName.getText().toString();
                    String facilityLocation = EditFacilityLocation.getText().toString();

                    // ensure valid input
                    if (!facilityName.isEmpty() && !facilityLocation.isEmpty()) {
                        facility = new Facility(facilityName, user, facilityLocation); // Create facility if valid
                        user.setFacility(facility); // Set the facility to the user
                        // TODO: Add facility to the database here or in a different method

                    }

                    // otherwise, don't add the thing :)
                    else {
                        Toast.makeText(getContext(), "Please enter both name and location", Toast.LENGTH_SHORT).show(); // TODO SHOULD WE TOAST FOR INVALID INPUT
                    }


                })
                .create();
    }
}
