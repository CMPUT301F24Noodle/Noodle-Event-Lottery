package com.example.myapplication.ui.user_profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;


/**
 * Author: Xavier Salm
 * Class for the activity for users to view their profile, and manage their facility.
 *
 */

// generally noticed a lot of cases of storyboards not matching attributes, or missing attributes... those will have to be added in later
public class MyProfileActivity extends AppCompatActivity {
    UserProfile user;
    Facility facility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile); // Ensure this layout file exists

        // Intents should not be needed to pass info, because we can get the user info from the database
        // might need intents to get the database


        user = new UserProfile(); // TODO Grab the user from the database using the device identifier

        TextView usernameText = findViewById(R.id.profile_user_full_name);
        TextView emailText = findViewById(R.id.profile_user_email);
        TextView phoneNumberText = findViewById(R.id.profile_user_contact_number);
        TextView addressText = findViewById(R.id.profile_user_address);

        TextView facilityNameText = findViewById(R.id.profile_facility_name);
        TextView facilityLocationText = findViewById(R.id.profile_facility_location);


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

        Button deleteFacilityButton = findViewById(R.id.profile_delete_facility_button);
        Button saveInfoButton = findViewById(R.id.profile_save_info_button);

        //Erin-Marie added this backButton so if it breaks anything let me know
        Button backButton = findViewById(R.id.profile_back_button);
        Switch toggleFacilitySwitch = findViewById(R.id.profile_facility_toggle_switch);

        // BUTTON CONFIRMING A DELETION OF THE USER'S FACILITY
        deleteFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() != null){
                    DeleteFacilityFragment deleteFacilityFragment = new DeleteFacilityFragment();

                    deleteFacilityFragment.show(getSupportFragmentManager(), "deleteFacility"); // now show the fragment
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
                            facility = new Facility(facilityName, user, facilityLocation);
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

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                finish();
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

                    // if the user doesn't have a facility, create one for them
                } else {
                    // if toggled off, make all facility fields and buttons invisible
                    facilityNameText.setVisibility(View.GONE);
                    facilityLocationText.setVisibility(View.GONE);
                    deleteFacilityButton.setVisibility(View.GONE);
                }
            }
        });

    }
}

