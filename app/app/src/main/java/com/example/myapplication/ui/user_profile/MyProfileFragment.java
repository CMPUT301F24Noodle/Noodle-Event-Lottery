package com.example.myapplication.ui.user_profile;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.databinding.FragmentMyProfileBinding;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserProfile;


import de.hdodenhof.circleimageview.CircleImageView;


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
    FacilityDB facilityDB;
    MainActivity main;
    BitmapHelper helper;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        inflater.inflate(R.layout.fragment_my_profile, container, false);

        FragmentMyProfileBinding binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // create the helper
        helper = new BitmapHelper();

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

        // display the generated image:
        CircleImageView profilePictureView = view.findViewById(R.id.my_profile_image);
        Bitmap ProfilePic = helper.loadProfilePicture(user);
        profilePictureView.setImageBitmap(ProfilePic);

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
        Switch toggleNotificationSwitch = view.findViewById(R.id.switch_notifications);
        toggleNotificationSwitch.setChecked(user.getAllowNotifs());

       //Added by Apoorv: If the user is already an organizer then this part will be visible and checked
        if (user.checkIsOrganizer()==true){
           toggleFacilitySwitch.setChecked(true);
           facilityNameText.setVisibility(View.VISIBLE);
           facilityLocationText.setVisibility(View.VISIBLE);
           deleteFacilityButton.setVisibility(View.VISIBLE);
       }

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
                    // only generate a new PP for the user if they don't have one uploaded
                    if(user.getHasProfilePic() == null){ // if somehow, the user didn't have this set (because they were created before this attribute was added, assume they don't have one)
                        user.setHasProfilePic(false);
                        // TODO THIS IS JUST HERE SO YOU KNOW WHEN THIS HAPPENS
                        Toast.makeText(main, "Reset profile pic status to no uploaded profile picture", Toast.LENGTH_LONG).show();
                    }
                    if(!user.getHasProfilePic()){
                        Bitmap newProfilePic = helper.generateProfilePicture(user);
                        profilePictureView.setImageBitmap(newProfilePic);
                    }


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

                            facility = new Facility(facilityName, facilityLocation, user);
                            user.setFacility(facility); // create a facility for the user!

                            // and set the text fields
                            facility.setFacilityName(facilityName);
                            facility.setLocation(facilityLocation);

                            //add facility to db
                            facilityDB.addFacility(facility);
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

                        user.setFacility(facility);
                        facilityDB.addFacility(facility);
                    }

                }

                // update the DB

                userDB.updateUserDocument(user);
                main.updateSidebarHeader(user.getName(), user.getEmail(), helper.loadProfilePicture(user));
                main.updateSidebarForUserType(user);

                //Display toast
                CharSequence toastText = "Save Successful";
                Toast saveComplete = Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT );
                saveComplete.show();
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

        // SWITCH TO TOGGLE NOTIFICATIONS
        toggleNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //the user wants notifications on
                    user.setAllowNotifs(Boolean.TRUE);
                } else { //the user wants notifications off
                    user.setAllowNotifs(Boolean.FALSE);
                }
            }
        });

        // SET UP PROFILE PICTURE AS A BUTTON FOR MANAGING PROFILE PICTURE
        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageProfilePictureFragment PPFragment = new ManageProfilePictureFragment();
                PPFragment.setUser(user);
                PPFragment.setMyProfilePictureView(profilePictureView);
                PPFragment.setUserDB(userDB);

                // Navigate to the fragment
                PPFragment.show(getParentFragmentManager(), "ProfilePictureManagementFragment");

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
        this.user = main.user;
        assert this.user != null;
        userDB = main.userDB;
        assert userDB != null;
        facilityDB = main.facilityDB;
    }


}

