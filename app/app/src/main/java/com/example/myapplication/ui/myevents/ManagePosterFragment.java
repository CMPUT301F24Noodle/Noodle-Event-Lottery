package com.example.myapplication.ui.myevents;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.R;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManagePosterFragment extends DialogFragment {
    Event event;
    EventDB eventDB;
    BitmapHelper helper;
    Boolean inFirebase; // a boolean to determine if the event has been saved in the DB

    // TODO TEST
    ActivityResultLauncher<Intent> galleryLauncher;

    ImageView posterImage;


    public void setEvent(Event event) {
        this.event = event;
    }

    public void setEventDB(EventDB eventDB) {
        this.eventDB = eventDB;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

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


                            // and then save it to the event!
                            event.setEventPoster(encodedBitmap);

                            // now save it to database (if it can be saved)
                            if(inFirebase){
                                eventDB.updateEvent(event);
                            }


                            // take the new bitmap and set the images to display the bitmap

                            posterImage.setImageBitmap(resizedProfilePicture);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_view_images, container, false);

        helper = new BitmapHelper();
        posterImage = view.findViewById(R.id.full_profile_image);

        // determine if you can actually save the event or not
        // this might be called from AddEventsFragment, which hasn't created the event yet
        if(event.getEventID() == null){
            inFirebase = false;
        }
        else{
            inFirebase = true;
        }

        if(!isAdded()){
            Toast.makeText(getContext(), "uh oh", Toast.LENGTH_SHORT).show();
        }

        if(getActivity() == null){
            Toast.makeText(getContext(), "dang", Toast.LENGTH_SHORT).show();
        }

        // if no poster, then hide the image:
        if(event.getEventPoster() == null){
            posterImage.setVisibility(View.GONE);
        }
        // or set the view to show the image
        else{
            String encodedPoster = event.getEventPoster();
            Bitmap poster = helper.decodeBase64StringToBitmap(encodedPoster);
            posterImage.setVisibility(View.VISIBLE);
            posterImage.setImageBitmap(poster);
        }




        // a button to let the user upload a poster
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
                event.setEventPoster(null); // remove the poster from the event
                posterImage.setVisibility(View.GONE); // make the view go away
                posterImage.setImageDrawable(null); // clear the view

                if(inFirebase){
                    eventDB.updateEvent(event);
                }
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



        return view;
    }
}
