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

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


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
public class MyProfileActivity extends AppCompatActivity{
    UserProfile user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile); // Ensure this layout file exists

        // Intents should not be needed to pass info, because we can get the user info from the database
        // might need intents to get the database


        user = new UserProfile(); // TODO Grab the user from the database using the device identifier

        TextView usernameText = findViewById(R.id.profile_user_full_name);
        TextView emailTest = findViewById(R.id.profile_user_email);
        TextView phoneNumberText = findViewById(R.id.profile_user_contact_number);
        TextView addressText = findViewById(R.id.profile_user_address);

        TextView facilityName = findViewById(R.id.profile_facility_name);
        TextView facilityLocation = findViewById(R.id.profile_facility_location);


        String userFullName = user.getFirstName()+" " + user.getLastName();

        // display user info
        usernameText.setText(userFullName);
        emailTest.setText(user.getEmail());
        phoneNumberText.setText(user.getPhoneNumber());
        addressText.setText(user.getAddress());

        if(user.getFacility() != null){
            Facility facility = user.getFacility();
            UserProfile organizer = facility.getOwner();
            String organizerName = organizer.getFirstName() + " " + organizer.getLastName();

            facilityName.setText(facility.getFacilityName());
            facilityLocation.setText(facility.getLocation());

        }
        else{
            // TODO should actually set the fields to invisible if no facility, but I'm waiting for DB integration first so I can set a listener for changes to the db
            facilityName.setText("None");
            facilityLocation.setText("None");

        }

        // set up buttons

        Button deleteFacilityButton = findViewById(R.id.profile_delete_facility_button);
        Button saveInfoButton = findViewById(R.id.profile_save_info_button);
        Switch toggleFacilitySwitch = findViewById(R.id.profile_facility_toggle_switch)

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
                // do something :)
            }
        });

        toggleFacilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // do something when switched on
                    int a =  1+1;
                } else {
                    // do something else when switched off
                    int b = 1+1;
                }
            }
        });




    }
}

