package com.example.myapplication;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Base64;

import com.example.myapplication.objects.UserProfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Xavier Salm
 * Contains several helper functions involving Bitmap objects
 * Mainly involves classes for converting bitmaps into other objects, or converting other objects into bitmaps
 * Primarily used for Image uploading/downloading, as well as profile picture generation
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
        // convert the string into its list of bytes
        byte[] encodedBytes = Base64.decode(encodedPicture, Base64.DEFAULT);

        // now take the list of bytes and convert it back into a bitmap!
        Bitmap profilePicture = BitmapFactory.decodeByteArray(encodedBytes, 0, encodedBytes.length);
        return profilePicture;
    }

    public String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // compress the bitmap (in this case quality is 100, meaning no quality loss) and send it to the output stream
        // the resizeBitmap function controls the quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        // now take the output stream and turn it into an array of bytes
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // and then take the array of bytes and turn it into a base 64 string (hexadecimal is base 16, for reference)
        // this allows for a more compact string
        String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedString;
    }

    public Bitmap UriToBitmap(Uri uri, Context context) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {
        // get the height and width of current bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // the scaled height and width of the resized bitmap
        int newWidth;
        int newHeight;

        // these effectively decide how compressed the images get when converted into bitmaps
        // higher max = better image quality but larger size in firebase
        // my camera took images with like a 800 * 2400 ish range, and that was too big to store in firebase
        int maxWidth = 600;
        int maxHeight = 600;

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap; // the bitmap was small enough that it does not need to be resized
        }

        // find the aspect ratio, which is important so that the picture keeps the same dimensions as it did before resizing
        float aspectRatio = (float) width / height;
        if (width > height) {
            newWidth = maxWidth;
            newHeight = Math.round(newWidth / aspectRatio); // effectively the same as height = height * (newWidth/width)
        } else {
            newHeight = maxHeight;
            newWidth = Math.round(newHeight * aspectRatio); // effectively the same as width = width * (newHeight/height)
        }

        // Scale the bitmap to a new bitmap with the same relative dimensions, but small enough that it will be able to get stored in firebase once encoded into a string
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        return resizedBitmap;
    }

    public Bitmap loadProfilePicture(UserProfile user){
        Bitmap profilePicture;
        // if they have a profile picture, decode it
        if(user.getHasProfilePic()){
            profilePicture = decodeBase64StringToBitmap(user.getEncodedPicture());
        }
        // if they dont have a profile picture, generate one
        else{
            profilePicture = generateProfilePicture(user);
        }
        return profilePicture;
    }

    /**
     * Author: Xavier Salm
     * generates a profile picture based on the first character of this users username
     * so a user with the username "steve" would have a profile picture that is just 's'
     *
     * @return profilePic: the newly generated profile picture
     */
    public Bitmap generateProfilePicture(UserProfile user){
        // get the character for the picture

        String firstChar;
        String name = user.getName();


        // set the dimensions of the picture
        // TODO: what should the dimensions be?
        int width = 100;
        int height = 100;

        // TODO: could change the config so that the profile picture has color
        // TODO important note, the colors you can use might be limited by the choice of bitmap.config (theres a chance you can only use like 16 different colors, idk)
        // create the bitmap that will become the new profile picture
        Bitmap profilePic = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // get a canvas to draw on the bitmap
        Canvas canvas = new Canvas(profilePic);

        // create the paint for the background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE); // TODO this color can be changed for UI

        // set the entire bitmap to a background color (because its not allowed to be transparent)
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // if the user has no name, just return the blank bitmap
        if(name == null || name.isEmpty()){
            return profilePic;
        }

        // otherwise get the character to paint onto the bitmap
        firstChar = Character.toString(user.getName().charAt(0));

        // now that the canvas is painted, we can actually draw on it
        Paint charPaint = new Paint();
        charPaint.setColor(Color.BLACK); // TODO this color can be changed for UI
        charPaint.setTextSize(50); // TODO this size can be changed for UI

        // get the width of the text
        float textWidth = charPaint.measureText(firstChar);

        // adjust the width and height by first accounting for the width and height of printed text
        // and then divide by 2 so that it is centered
        float adjustedWidth = (width - textWidth) / 2;
        float adjustedHeight = (height - (charPaint.descent() + charPaint.ascent())) / 2;

        // paint the string onto the canvas!
        canvas.drawText(firstChar, adjustedWidth, adjustedHeight, charPaint);

        // return the generated profile picture
        return profilePic;
    }
}
