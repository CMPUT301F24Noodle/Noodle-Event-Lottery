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
// TODO set up a ping system to let the activity know when the fragment has closed (for updating the displayed values)
// TODO add more facility attributes?
public class DeleteFacilityFragment extends DialogFragment {
    private Facility facility;
    private UserProfile user;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_delete_facility, null);

        // get the user
        user = new UserProfile(); // TODO get the existing profile from database using device ID
        facility = user.getFacility();


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Are you sure you want to delete your facility?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    user.removeFacility(facility);
                    // TODO: make sure removed from database here? or elsewhere?

                })
                .create();
    }
}

