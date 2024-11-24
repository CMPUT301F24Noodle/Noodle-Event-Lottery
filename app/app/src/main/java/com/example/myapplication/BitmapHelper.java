package com.example.myapplication;

import static java.security.AccessController.getContext;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

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
    public Bitmap decodeBase64StringToBitmap(String encodedPicture){
        byte[] decodedBytes = Base64.decode(encodedPicture, Base64.DEFAULT); // convert the string into bytes
        Bitmap profilePicture = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length); // and then turn those bytes into a bitmap
        return  profilePicture;
    }

    public String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // todo what quality makes sense here?
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap UriToBitmap(Uri uri) throws IOException {
        ContentResolver contentResolver = (ContentResolver) getContext().getDomainCombiner();
        InputStream inputStream = contentResolver.openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }
}
