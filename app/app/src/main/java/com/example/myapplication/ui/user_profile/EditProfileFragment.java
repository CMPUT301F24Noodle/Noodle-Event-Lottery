package com.example.myapplication.ui.user_profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

// TODO add a class header
// TODO add all strings here and in xml to string resource file
// TODO set up a ping system to let the activity know when the fragment has closed (for updating the displayed values)
// TODO OKay so uh this was not in story boards, but it works a little easier so uh we doing it
/**
 * Author: Xavier Salm
 * Class for the fragment to edit a profile
 *
 */
public class EditProfileFragment extends DialogFragment {
    private Facility facility;
    private UserProfile user;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_edit_profile, null);

        // get the user
        user = new UserProfile(); // TODO get the existing profile from database using device ID
        facility = user.getFacility();

        // get all the text views
        EditText editProfileFirstName = view.findViewById(R.id.edit_text_profile_first_name);
        EditText editProfileLastName = view.findViewById(R.id.edit_text_profile_last_name);
        EditText editProfileEmail = view.findViewById(R.id.edit_text_profile_email);

        // set the text to current values
        editProfileFirstName.setText(user.getFirstName());
        editProfileLastName.setText(user.getLastName());
        editProfileEmail.setText(user.getEmail());


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Facility")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String firstName = editProfileFirstName.getText().toString();
                    String lastName = editProfileLastName.getText().toString();
                    String email = editProfileEmail.getText().toString();

                    // only update text for non empty input
                    if (!firstName.isEmpty()) {
                        user.setFirstName(firstName);
                    }
                    if(!lastName.isEmpty()) {
                        user.setLastName(lastName);
                    }
                    if(!email.isEmpty()) {
                        user.setEmail(email);
                    }
                    // TODO edit database

                })
                .create();
    }
}

