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
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * Author: Xavier Salm
 * Class for the activity for users to view their profile, and manage their facility.
 * TODO: ADD METHOD TO GET DEVICE ID
 * TODO add all strings here and in xml to string resource file
 * TODO: add a way to know if a facility was succesfully created or removed (to make the now useless buttons invisible) and to update the text views for facility
 * TODO
 */
public class MyProfileActivity extends AppCompatActivity{
    UserProfile user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile); // Ensure this layout file exists

        // Intents should not be needed to pass info, because we can get the user info from the database

        //Intent intent = getIntent();
        //String cityName = intent.getStringExtra("cityName");

        user = new UserProfile(); // TODO Grab the user from the database using the device identifier

        TextView usernameText = findViewById(R.id.profile_username_text);
        TextView emailTest = findViewById(R.id.profile_email_text);
        TextView phoneNumberText = findViewById(R.id.profile_phone_number_text);
        TextView addressText = findViewById(R.id.profile_address_text);

        String userFullName = user.getFirstName()+" " + user.getLastName();

        usernameText.setText(userFullName);
        emailTest.setText(user.getEmail());

        phoneNumberText.setVisibility(View.INVISIBLE); // TODO Add phone number to user attributes
        addressText.setVisibility(View.INVISIBLE); // TODO Add address to user attributes

        Button createFacilityButton = findViewById(R.id.create_facility_button);
        Button editFacilityButton = findViewById(R.id.edit_facility_button);
        Button deleteFacilityButton = findViewById(R.id.delete_facility_button);


        createFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() == null){ // for now, nothing happens if the user has a facility
                    CreateFacilityFragment createFacilityFragment = new CreateFacilityFragment();

                    createFacilityFragment.show(getSupportFragmentManager(), "createFacility"); // now show the fragment
                }
            }
        });

        editFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() == null){ // for now, nothing happens if the user has a facility
                    EditFacilityFragment editFacilityFragment = new EditFacilityFragment();

                    EditFacilityFragment.show(getSupportFragmentManager(), "editFacility"); // now show the fragment
                }
            }
        });

        deleteFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFacility() == null){ // for now, nothing happens if the user has a facility
                    CreateFacilityFragment createFacilityFragment = new CreateFacilityFragment();

                    createFacilityFragment.show(getSupportFragmentManager(), "createFacility"); // now show the fragment
                }
            }
        });




    }
}

