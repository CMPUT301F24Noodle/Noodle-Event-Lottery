package com.example.myapplication;

import static java.security.AccessController.getContext;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import com.example.myapplication.objects.userProfileClasses.UserProfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Xavier Salm
 * Contains several helper functions involving Bitmap objects
 * Mainly involves classes for converting bitmaps into other objects, or converting other objects into bitmaps
 * Primarily used for Image uploading/downloading, as well as QR code generation
 */
public class BitmapHelper {
    /**
     * Author: Xavier Salm
     * converts the base64 string representation of an image into a bitmap that can be displayed
     *
     * @param encodedPicture: the encoded string that represents the profile picture
     * @return profilePicture: the profile picture encoded in the base64 string
     */

    public Bitmap decodeBase64StringToBitmap(String encodedPicture) {
        if (encodedPicture == null || encodedPicture.isEmpty()) {
            throw new IllegalArgumentException("Encoded picture string is null or empty.");
        }
        byte[] decodedBytes = Base64.decode(encodedPicture, Base64.DEFAULT);
        Bitmap profilePicture = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return profilePicture;
    }

    public String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap UriToBitmap(Uri uri, Context context) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // these effectively decide how compressed the images get when converted into bitmaps
        // higher max = better image quality but larger size in firebase
        int maxWidth = 600;
        int maxHeight = 600;

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap; // No need to resize
        }

        float aspectRatio = (float) width / height;
        if (width > height) {
            width = maxWidth;
            height = Math.round(width / aspectRatio); // ensure that the picture keeps the same ratio
        } else {
            height = maxHeight;
            width = Math.round(height * aspectRatio); // ensure that the picture keeps the same ratio
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public Bitmap loadProfilePicture(UserProfile user){
        Bitmap profilePicture;
        // if they have a profile picture, decode it
        if(user.getHasProfilePic()){
            profilePicture = user.decodeBase64StringToBitmap(user.getEncodedPicture());
        }
        // if they dont have a profile picture, generate one
        else{
            profilePicture = user.generateProfilePicture();
        }
        return profilePicture;
    }
}
