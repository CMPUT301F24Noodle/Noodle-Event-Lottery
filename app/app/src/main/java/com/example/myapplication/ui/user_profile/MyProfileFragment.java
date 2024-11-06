package com.example.myapplication.ui.user_profile;


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

import com.example.myapplication.R;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.databinding.FragmentMyProfileBinding;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;


/**
 * Author: Xavier Salm
 * Class for the activity for users to view their profile, and manage their facility.
 * Uses the activity_my_profile.xml and fragment_delete_facility.xml layout files
 * USERSTORIES: US.01.02.01, US.01.02.02, US.02.01.03,
 *
 */

public class MyProfileFragment extends Fragment {
    UserProfile user;
    UserDB userDB;
    Facility facility;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        inflater.inflate(R.layout.fragment_my_profile, container, false);
        //View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        FragmentMyProfileBinding binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // get all variables
        user = new UserProfile();

        // start of text fields
        TextView usernameText = view.findViewById(R.id.profile_user_full_name);
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

        //Erin-Marie added this backButton so if it breaks anything let me know
        //Button backButton = findViewById(R.id.profile_back_button);

        Switch toggleFacilitySwitch = view.findViewById(R.id.profile_facility_toggle_switch);

        // BUTTON CONFIRMING A DELETION OF THE USER'S FACILITY
        deleteFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() != null){
                    DeleteFacilityFragment deleteFacilityFragment = new DeleteFacilityFragment();

                    deleteFacilityFragment.show(getParentFragmentManager(), "deleteFacility"); // now show the fragment
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

                // TODO update the DB
            }
        });

        //backButton.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v){
           //     finish();
         //   }
       // });


        // SWITCH TO TOGGLE FACILITY FIELDS DISPLAY
        toggleFacilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // if toggled on, make all facility fields and buttons visible
                    facilityNameText.setVisibility(View.VISIBLE);
                    facilityLocationText.setVisibility(View.VISIBLE);
                    deleteFacilityButton.setVisibility(View.VISIBLE);

                    // if the user doesn't have a facility, create one for them
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


}

