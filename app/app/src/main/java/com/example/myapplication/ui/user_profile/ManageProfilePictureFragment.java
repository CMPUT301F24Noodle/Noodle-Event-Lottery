package com.example.myapplication.ui.user_profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.R;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.auth.User;

import android.Manifest;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageProfilePictureFragment extends DialogFragment {
    UserProfile user;
    CircleImageView MyProfilePictureView; // so that the view in MyProfileFragment also gets changed
    UserDB userDB;
    BitmapHelper helper;

    // TODO TEST
    private ActivityResultLauncher<Intent> galleryLauncher;

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public void setMyProfilePictureView(CircleImageView myProfilePictureView) {
        MyProfilePictureView = myProfilePictureView;
    }

    public void setUserDB(UserDB userDB) {
        this.userDB = userDB;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_view_images, container, false);

        helper = new BitmapHelper();
        ImageView fullProfilePicture = view.findViewById(R.id.full_profile_image);

        // display the picture
        Bitmap profilePicture = helper.loadProfilePicture(user); // either decodes the profile picture or provides a generated one
        fullProfilePicture.setImageBitmap(profilePicture);



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
                user.setEncodedPicture(null); // remove their old profile picture
                user.setHasProfilePic(false); // they should now get generated profile pictures

                // now update the image views with a generated profile picture
                Bitmap generatedPP = helper.generateProfilePicture(user);
                MyProfilePictureView.setImageBitmap(generatedPP);
                fullProfilePicture.setImageBitmap(generatedPP);

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
                            user.setEncodedPicture(encodedBitmap);
                            user.setHasProfilePic(true);

                            // now save it to database
                            userDB.updateUserDocument(user);

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
