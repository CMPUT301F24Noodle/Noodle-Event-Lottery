package com.example.myapplication.ui.user_profile;

import android.app.Activity;
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

import com.example.myapplication.R;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.auth.User;

import android.Manifest;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageProfilePictureFragment extends DialogFragment {
    UserProfile user;
    CircleImageView MyProfilePictureView; // so that the view in MyProfileFragment also gets changed

    // TODO TEST
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public void setMyProfilePictureView(CircleImageView myProfilePictureView) {
        MyProfilePictureView = myProfilePictureView;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.delete_view_images, container, false);

        ImageView fullProfilePicture = view.findViewById(R.id.full_profile_image);

        // TODO test
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, proceed with gallery access
                    } else {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //TODO test
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        ImageView imageView = getView().findViewById(R.id.full_profile_image);
                        imageView.setImageURI(selectedImage);
                    }
                }
        );

        // a button to let the user upload a profile picture
        Button editButton = view.findViewById(R.id.edit_picture_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO TEST
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    galleryLauncher.launch(intent);
                }
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
                Bitmap generatedPP = user.generateProfilePicture();
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

        return view;
    }
}
