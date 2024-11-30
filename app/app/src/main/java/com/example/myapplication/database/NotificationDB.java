package com.example.myapplication.database;

import static com.google.firebase.firestore.FieldValue.arrayRemove;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.objects.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Author: Erin-Marie
 * Class that handles database access for notification collection
 * QUESTION: should there be a reload function or something? currently updates only seen if user leaves the fragment and comes back
 */
public class NotificationDB {

    //For logcat
    private final static String TAG = "NotificationDB";

    public DocumentReference userDocumentReference;
    public DBConnection storeConnection;
    public CollectionReference allNotifications;
    public FirebaseFirestore db;
    public UserProfile currentUser;
    public String uuid;
    public ArrayList<Notification> myNotifs = new ArrayList<Notification>();
    public ArrayList<Notification> myNewNotifs = new ArrayList<Notification>();

    /**
     * Class constructor
     * @param connection DBconnection from mainActivity
     */
    public NotificationDB(DBConnection connection) {
        //Gets a reference to the current users document in the db
        this.storeConnection = connection;
        this.allNotifications = connection.getAllNotificationsCollection();
        this.uuid = connection.getUUID();
        this.userDocumentReference = connection.getUserDocumentRef();
        this.db = connection.getDB();
        this.myNotifs = getUserNotifications();
        this.myNewNotifs = getUserNewNotifications();
    }

    /**
     * Author: Erin-Marie
     * adds a notification to the db collection AllNotifications
     * @param notif the notification instance to be added
     */
    public void addNotification(Notification notif){
        allNotifications.add(notif);
        Log.v("DatabaseRead", "Successfully added notification");
    }

    /**
     * Author: Erin-Marie
     * Queries the db for all notifications where the current user is a recipient, and HAS NOT recieved the notification yet
     * This method is for getting the notifications for the users system notifications
     * @return an array list of all the notifications the user has not seen yet
     */
    public ArrayList<Notification> getUserNewNotifications(){
        //query all notifs for the ones where the current user is a recipient, and order by time sent
        Query getMyNotifs = allNotifications.whereArrayContains("notReadBy", userDocumentReference).orderBy("sentTime", Query.Direction.DESCENDING);
        getQuery(getMyNotifs, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //empty the current list of notifs so there are not duplicates
                myNewNotifs.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //add each notification to the arraylist
                    Notification notification = document.toObject(Notification.class);
                    myNewNotifs.add(notification);
                    //mark the user as having seen the notification
                    //remove the user from the recipients list
                    //(document.getReference()).update("recipients", arrayRemove(userDocumentReference));
                    //add the user to the readBy list
                    (document.getReference()).update("notReadBy", arrayRemove(userDocumentReference));

                    Log.v(TAG, "new" + document.getId() + " => " + document.getData());
                }
                if (myNewNotifs.isEmpty()){
                    Log.v(TAG, "user has no new notifications");
                }
            }
        });
        return this.myNewNotifs;
    }

    /**
     * Author: Erin-Marie
     * Queries the db for ALL notifications where the current user is a recipient
     * This method is for getting the users notifications to display in the Notifications Fragment
     * @return an array list of all the notifications the user has not seen yet
     */
    public ArrayList<Notification> getUserNotifications(){
        //query all notifs for the ones where the current user is a recipient, and order by time sent
        Query getMyNotifs = allNotifications
                .whereArrayContains("recipients", userDocumentReference)
                .orderBy("sentTime", Query.Direction.DESCENDING);
        getQuery(getMyNotifs, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //empty the current list of notifs so there are not duplicates
                myNotifs.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //add each notification to the arraylist
                    Notification notification = document.toObject(Notification.class);
                    myNotifs.add(notification);

                    Log.v(TAG, document.getId() + " => " + document.getData());
                }
            }
        });
        return this.myNotifs;
    }

    /**
     * Author: Erin-Marie
     * this function will get the documents from any query passed to it
     * does not return, but will give the listener argument the task back if it is successful
     * @param query Query object you want to execute
     * @param listener OnCompleteListener from calling method, calling method needs a
     */
    public void getQuery(Query query, OnCompleteListener<QuerySnapshot> listener){
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listener.onComplete(task);

                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public ArrayList<Notification> getMyNewNotifs() {
        return myNewNotifs;
    }

    public void setMyNewNotifs(ArrayList<Notification> myNewNotifs) {
        this.myNewNotifs = myNewNotifs;
    }

    public ArrayList<Notification> getMyNotifs() {
        return myNotifs;
    }

    public void setMyNotifs(ArrayList<Notification> myNotifs) {
        this.myNotifs = myNotifs;
    }
}
