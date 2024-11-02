package com.example.myapplication.ui.user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// TODO: YOU CAN JUST MAKE IT AN INTERFACE THATS IMPLEMENTED HERE, THEN IT COULD UPDATE THE VIEWS AND BUTTON IN MAIN
// TODO THIS WOULD STILL NEED DATABASE INTEGRATION SPECIFICALLY IF THE USER PROFILE GETS DELETED, BUT OTHER THAN THAT, ITS GOOD

/**
 * Author: Xavier Salm
 * Class for the activity for users to view their profile, and manage their facility.
 * TODO: ADD METHOD TO GET DEVICE ID
 * TODO add all strings here and in xml to string resource file
 * TODO: add a way to know if a facility was succesfully created or removed (to make the now useless buttons invisible) and to update the text views for facility
 * TODO could do the above by just adding a database listener that updates things when it detects a change in database
 * TODO add other attributes to profile for phone number or address for example
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


        String userFullName = user.getFirstName()+" " + user.getLastName();

        // display user info
        usernameText.setText(userFullName);
        emailText.setText(user.getEmail());
        phoneNumberText.setText(user.getPhoneNumber());
        addressText.setText(user.getAddress());

        if(user.getFacility() != null){
            facility = user.getFacility();

            facilityNameText.setText(facility.getFacilityName());
            facilityLocationText.setText(facility.getLocation());

        }

        // set up buttons

        Button deleteFacilityButton = findViewById(R.id.profile_delete_facility_button);
        Button saveInfoButton = findViewById(R.id.profile_save_info_button);
        Switch toggleFacilitySwitch = findViewById(R.id.profile_facility_toggle_switch);

        deleteFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() != null){
                    DeleteFacilityFragment deleteFacilityFragment = new DeleteFacilityFragment();

                    deleteFacilityFragment.show(getSupportFragmentManager(), "deleteFacility"); // now show the fragment
                }
            }
        });

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO its REALLY annoying that users have a first name and then a last name, will change this later but I don't want to ping Erin so late tonight :)
                // HELLO this is erin i can change it to being just name? also ping me anytime, I dont have notifs for discord on on my phone, so i would not see it until morning anyways
                //String username =

                // Get the new values and set them in userProfile
                String email = emailText.getText().toString();
                String number = phoneNumberText.getText().toString();
                String address = addressText.getText().toString();

                // TODO verify proper input (no empty fields)
                user.setEmail(email);
                user.setPhoneNumber(number);
                user.setAddress(address);

                if(toggleFacilitySwitch.isChecked()){ // if the user can see facility stuff
                    // Get the new values and set them in the user's facility
                    String facilityName = facilityNameText.getText().toString();
                    String facilityLocation = facilityLocationText.getText().toString();

                    if(user.getFacility() == null){
                        if(!facilityName.equals(" ") && !facilityLocation.equals(" ")){
                            facility = new Facility(facilityName, user, facilityLocation);
                            user.setFacility(facility); // create a facility for the user!

                            // and set the text fields
                            facility.setFacilityName(facilityName);
                            facility.setLocation(facilityLocation);
                        }
                    }

                    else{
                        // otherwise, just set the text fields
                        facility.setFacilityName(facilityName);
                        facility.setLocation(facilityLocation);
                    }

                }
            }
        });

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

