package com.example.myapplication.ui.user_profile;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.databinding.FragmentMyProfileBinding;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;


/**
 * Author: Xavier Salm
 * Class for the fragment for users to view/edit their profile, and create/manage their facilities.
 * Uses the activity_my_profile.xml and fragment_delete_facility.xml layout files
 * USERSTORIES: US.01.02.01, US.01.02.02, US.02.01.03,
 *
 */

public class MyProfileFragment extends Fragment {
    UserProfile user;
    UserDB userDB;
    Facility facility;

    MainActivity main;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        inflater.inflate(R.layout.fragment_my_profile, container, false);

        FragmentMyProfileBinding binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // get the user and userDB
        getUserUserDB();

        // start of text fields
        TextView usernameText = view.findViewById(R.id.profile_user_name);
        TextView emailText = view.findViewById(R.id.profile_user_email);
        TextView phoneNumberText = view.findViewById(R.id.profile_user_contact_number);
        TextView addressText = view.findViewById(R.id.profile_user_address);

        TextView facilityNameText = view.findViewById(R.id.profile_facility_name);
        TextView facilityLocationText = view.findViewById(R.id.profile_facility_location);


        // display user info
        usernameText.setText(user.getName());
        emailText.setText(user.getEmail());
        phoneNumberText.setText(user.getPhoneNumber());
        addressText.setText(user.getAddress());

        // if they have a facility, set their facility info
        if(user.getFacility() != null){
            facility = user.getFacility();

            facilityNameText.setText(facility.getFacilityName());
            facilityLocationText.setText(facility.getLocation());

        }

        // set up buttons

        Button deleteFacilityButton = view.findViewById(R.id.profile_delete_facility_button);
        Button saveInfoButton = view.findViewById(R.id.profile_save_info_button);

        Switch toggleFacilitySwitch = view.findViewById(R.id.profile_facility_toggle_switch);

        // BUTTON CONFIRMING A DELETION OF THE USER'S FACILITY
        deleteFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if they actually have a facility
                if(user.getFacility() != null) {

                    // create a dialogue box for them to confirm their choice
                    new AlertDialog.Builder(requireContext()) //
                            .setTitle("Delete Facility")
                            .setMessage("Are you sure you would like to delete your facility?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // now that they have confirmed, remove the facility
                                    user.removeFacility(facility);

                                    // and update the db
                                    userDB.updateUserDocument(user);

                                    // set the facility fields to empty so the hint is displayed again
                                    facilityNameText.setText("");
                                    facilityLocationText.setText("");
                                }
                            })
                            .setNegativeButton("Cancel", null) // closes the dialog on cancel
                            .show();
                }
            }
        });

        // BUTTON FOR SAVING ANY EDITS MADE TO THE TEXT FIELDS FOR USER OR FACILITY
        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the new values and set them in userProfile
                String username = usernameText.getText().toString();
                String email = emailText.getText().toString();
                String number = phoneNumberText.getText().toString();
                String address = addressText.getText().toString();

                // verify input
                if(!username.isEmpty()){
                    user.setName(username);
                }
                if(!email.isEmpty()){
                    user.setEmail(email);
                }

                if (!number.isEmpty()){
                    user.setPhoneNumber(number);
                }
                else{
                    user.setPhoneNumber(""); // users can optionally share their phone number, and leaving the phone number field clear allows users to unshare their phone number
                }

                if (!address.isEmpty()){
                    user.setAddress(address);
                }

                // set the text fields
                usernameText.setText(user.getName());
                emailText.setText(user.getEmail());
                phoneNumberText.setText(user.getPhoneNumber());
                addressText.setText(user.getAddress());

                if(toggleFacilitySwitch.isChecked()){ // if the user can see facility stuff
                    // Get the new values and set them in the user's facility
                    String facilityName = facilityNameText.getText().toString();
                    String facilityLocation = facilityLocationText.getText().toString();

                    if(user.getFacility() == null){
                        if(!facilityName.isEmpty() && !facilityLocation.isEmpty()){

                            facility = new Facility(facilityName, facilityLocation);
                            user.setFacility(facility); // create a facility for the user!

                            // and set the text fields
                            facility.setFacilityName(facilityName);
                            facility.setLocation(facilityLocation);
                        }
                        else{
                            // if the save was unsuccessful, reset both fields
                            facilityNameText.setText("");
                            facilityLocationText.setText("");
                        }
                    }

                    else{
                        // validate input
                        if(!facilityName.isEmpty()){
                            facility.setFacilityName(facilityName);
                        }
                        if(!facilityLocation.isEmpty()){
                            facility.setLocation(facilityLocation);
                        }

                        // actually set the next fields
                        facilityNameText.setText(facility.getFacilityName());
                        facilityLocationText.setText(facility.getLocation());
                    }

                }

                // update the DB
                userDB.updateUserDocument(user);
            }
        });

        // SWITCH TO TOGGLE FACILITY FIELDS DISPLAY
        toggleFacilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // if toggled on, make all facility fields and buttons visible
                    facilityNameText.setVisibility(View.VISIBLE);
                    facilityLocationText.setVisibility(View.VISIBLE);
                    deleteFacilityButton.setVisibility(View.VISIBLE);

                    if(user.getFacility() == null){
                        new AlertDialog.Builder(requireContext()) //
                                .setTitle("Become an Organizer")
                                .setMessage("Become an organizer and create events by entering in the name and location of your facility!")
                                .setNegativeButton("Continue", null) // closes the dialog on cancel
                                .show();
                    }


                } else {
                    // if toggled off, make all facility fields and buttons invisible
                    facilityNameText.setVisibility(View.GONE);
                    facilityLocationText.setVisibility(View.GONE);
                    deleteFacilityButton.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    /**
     * Author: Xavier Salm
     * Gets the user and userDB object from main
     */
    public void getUserUserDB(){
        main = (MainActivity) getActivity();
        assert main != null;
        user = main.user;
        assert user != null;
        userDB = main.userDB;
        assert userDB != null;
    }

}

