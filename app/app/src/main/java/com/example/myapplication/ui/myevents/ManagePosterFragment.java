package com.example.myapplication.ui.myevents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.R;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManagePosterFragment extends DialogFragment {
    Event event;
    CircleImageView MyProfilePictureView; // so that the view in MyProfileFragment also gets changed
    EventDB eventDB;
    BitmapHelper helper;

    // TODO TEST
    private ActivityResultLauncher<Intent> galleryLauncher;

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setMyProfilePictureView(CircleImageView myProfilePictureView) {
        MyProfilePictureView = myProfilePictureView;
    }

    public void setEventDB(EventDB eventDB) {
        this.eventDB = eventDB;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_view_images, container, false);

        helper = new BitmapHelper();
        ImageView fullProfilePicture = view.findViewById(R.id.full_profile_image);

        // display the picture
        //Bitmap profilePicture = helper.loadProfilePicture(user); // either decodes the profile picture or provides a generated one
        //fullProfilePicture.setImageBitmap(profilePicture);



        // a button to let the user upload a profile picture
        // launches an intent that allows users to open their photo gallery
        // doesn't need permissions because its just photos
        Button editButton = view.findViewById(R.id.edit_picture_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO TEST
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                galleryLauncher.launch(intent);
            }
        });

        // a button to remove the picture
        Button deleteButton = view.findViewById(R.id.delete_picture_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        // a button to leave the fragment
        Button backButton = view.findViewById(R.id.picture_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // close the fragment
            }
        });

        //TODO
        // what happens after a the user selects an image from their gallery
        // the Uri of their selected image is gotten through result.getData().getData()
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();

                        try {
                            // you have to do error catching because UriToBitmap can throw an error
                            Context context = getContext();

                            assert context != null;
                            Bitmap originalProfilePicture = helper.UriToBitmap(selectedImage, context);
                            Bitmap resizedProfilePicture = helper.resizeBitmap(originalProfilePicture); // need to resize so the string representation isn't too long

                            // encode the bitmap
                            String encodedBitmap = helper.encodeBitmapToBase64(resizedProfilePicture);


                            // and then save it to the user!
                            event.setEventPoster(encodedBitmap);

                            // now save it to database
                            
                            eventDB.updateEvent(event);

                            // take the new bitmap and set the images to display the bitmap
                            fullProfilePicture.setImageBitmap(resizedProfilePicture);
                            MyProfilePictureView.setImageBitmap(resizedProfilePicture);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
        );

        return view;
    }
}
