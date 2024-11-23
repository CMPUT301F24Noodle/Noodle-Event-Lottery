package com.example.myapplication.database;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Author: Xavier Salm
 * Class that handles uploading and retrieving pictures to and from firebase storage
 */
public class ImagesDB {

    // For logcat
    private final static String TAG = "PictureDB";

    public DocumentReference userDocumentReference;
    public DBConnection storeConnection;
    // TODO does this need a reference?
    //public CollectionReference allNotifications;
    public FirebaseFirestore db;
    public UserProfile currentUser;
    public String uuid;


    /**
     * Class constructor
     * @param connection DBconnection from mainActivity
     */
    public ImagesDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.storeConnection = connection;
        //this.allNotifications = connection.getAllNotificationsCollection();
        this.uuid = connection.getUUID();
        this.userDocumentReference = connection.getUserDocumentRef();
        this.db = connection.getDB();
        //this.myNotifs = getUserNotifications();
        //this.myNewNotifs = getUserNewNotifications();
    }

    /**
     * uploads a file and stores it in firebase storage
     * @param file the file to be stored in firebase storage
     */
    public URI uploadFile(File file){
        // get the file path + file uri from the file
        // then get a firebase file reference
        // then upload the file to that reference

        // return the firebase URI of the gotten thing
    }

    /**
     * given the URI and the view you want the image uploaded to, displays the image
     * hopefully uses glide or picasso
     * method specifically for ImageViews
     * @param view the view for the new image
     * @param uri the uri pointing to the file in firebase
     */
    public void retrieveImage(ImageView view,URI uri){

    }

    /**
     * given the URI and the view you want the image uploaded to, displays the image
     * hopefully uses glide or picasso
     * method specifically for CircleImageViews
     * @param view the view for the new image
     * @param uri the uri pointing to the file in firebase
     */
    public void retrieveImage(CircleImageView view, URI uri){

    }



    /**
     * Helper method that converts a Bitmap object into a JPEG file that can be uploaded to firebase storage
     * @param bitmap the bitmap that should be converted
     */
    public File bitmapToJPEG(Bitmap bitmap){

    }


}
