package com.example.myapplication.ui.user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;

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
public class MyProfileActivity extends AppCompatActivity{
    UserProfile user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile); // Ensure this layout file exists

        // Intents should not be needed to pass info, because we can get the user info from the database
        // might need intents to get the database


        user = new UserProfile(); // TODO Grab the user from the database using the device identifier

        TextView usernameText = findViewById(R.id.profile_full_name_text);
        TextView emailTest = findViewById(R.id.profile_email_text);

        TextView facilityName = findViewById(R.id.profile_facility_name_text);
        TextView facilityLocation = findViewById(R.id.profile_facility_location_text);
        TextView facilityOrganizer = findViewById(R.id.profile_facility_organizer_text);

        String userFullName = user.getFirstName()+" " + user.getLastName();

        // display user info
        usernameText.setText(userFullName);
        emailTest.setText(user.getEmail());

        if(user.getFacility() != null){
            Facility facility = user.getFacility();
            UserProfile organizer = facility.getOwner();
            String organizerName = organizer.getFirstName() + " " + organizer.getLastName();

            facilityName.setText(facility.getFacilityName());
            facilityLocation.setText(facility.getLocation());
            facilityOrganizer.setText(organizerName);
        }
        else{
            // TODO should actually set the fields to invisible if no facility, but I'm waiting for DB integration first so I can set a listener for changes to the db
            facilityName.setText("None");
            facilityLocation.setText("None");
            facilityOrganizer.setText("None");
        }

        // set up buttons
        Button createFacilityButton = findViewById(R.id.create_facility_button);
        Button editFacilityButton = findViewById(R.id.edit_facility_button);
        Button deleteFacilityButton = findViewById(R.id.delete_facility_button);
        Button editProfileButton = findViewById(R.id.edit_profile_button);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditProfileFragment editProfileFragment = new EditProfileFragment();

                editProfileFragment.show(getSupportFragmentManager(), "editProfile"); // now show the fragment

            }
        });


        createFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() == null){ // TODO make this button invisible if there is a facility
                    CreateFacilityFragment createFacilityFragment = new CreateFacilityFragment();

                    createFacilityFragment.show(getSupportFragmentManager(), "createFacility"); // now show the fragment
                }
            }
        });

        editFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() != null){ // TODO make this button invisible if no facility
                    EditFacilityFragment editFacilityFragment = new EditFacilityFragment();

                    editFacilityFragment.show(getSupportFragmentManager(), "editFacility"); // now show the fragment
                }
            }
        });

        deleteFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() != null){ // TODO make this button invisible if no facility
                    CreateFacilityFragment createFacilityFragment = new CreateFacilityFragment();

                    createFacilityFragment.show(getSupportFragmentManager(), "deleteFacility"); // now show the fragment
                }
            }
        });




    }
}

