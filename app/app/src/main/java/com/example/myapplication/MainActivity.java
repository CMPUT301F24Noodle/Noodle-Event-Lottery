package com.example.myapplication;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.notificationClasses.Notification;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * Author: Erin-Marie
 * Main Activity of the app
 * Controls the app navigation
 * Initializes all user data, including profile, event lists, and notifications
 */
public class MainActivity extends AppCompatActivity {

    //for logcat
    private String TAG = "Main Activity";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public DBConnection connection;
    public UserDB userDB; // userDB instance for the current user
    public EventDB eventDB;
    public FacilityDB facilityDB;
    public NotificationDB notifDB;
    public String uuid;
    public UserProfile user;
    public NotificationManager notificationManager;
    private final String CHANNEL_ID = "NoodleNotifs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // will get the current Users Profile, initialize db connections
        setUpDB();

        // Create the users NotificationChannel
        setUpNotifChannel();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.g
        // TODONE: add UserProfile to the nav drawer so it can be selected and we can view the UserProfile fragment
        //MAYBE: to add nav_home activity back into the menu, uncomment the MAYBE below, as well as both MAYBE in mobile_navigation.XML and in activity_drawer_main.XML
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_myevents, R.id.nav_registered, R.id.nav_notifications)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Author: Erin-Marie
     * Sets up the db when main activity launches
     * gets the current users UserProfile object based on the data stored in the db
     * If the user is not in the db, it adds them to the db.
     * No return, but does set the currentUser attributes of MainActivity and UserDB
     */
    public void setUpDB() {
        // Add the user to the db, added by Erin-Marie, if it breaks everything its my
        // fault
        this.connection = new DBConnection(this); // connection to the base db
        this.userDB = connection.getUserDB(); // the current users collection reference
        this.eventDB = connection.getEventDB();
        //THESE will be removed
        this.notifDB = connection.getNotifDB();
        this.facilityDB = connection.getFacilityDB();

        this.uuid = connection.getUUID(); // store the current users uuid
        // check if the user is already in the db
        this.userDB.checkUserExists(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot != null) { // user is already in the database
                    // fetch their profile data, make it a userprofile object, and store it as
                    // currentUser
                    // done through addUser() method in order to get the value returned from checkUserExists()
                    userDB.setCurrentProfile(snapshot);
                    user = userDB.getCurrentUser();
                    eventDB.getUserEnteredEvents(user); //initiate their list of entered events
                    eventDB.getUserOrgEvents(user); //initiate their list of organized events
                    connection.setUser(user);
                    notifDB.getUserNotifications(); //intitiate their list of all notifications
                    if (user.getAllowNotifs() == Boolean.TRUE) {
                        notifDB.getUserNewNotifications(); //get the list of users unseen notifications
                    }


                    Log.v("SetUpDB", "Set profile for existing user");

                } else { // User is not already in the database
                    // create a new profile objet, and store it in the db
                    // done through addUser() method in order to get the value returned from checkUserExists()
                    userDB.addCurrentUser();
                    user = userDB.getCurrentUser();
                    eventDB.getUserEnteredEvents(user);
                    eventDB.getUserOrgEvents(user);
                    connection.setUser(user);
                    Log.v("SetUpDB", "Set profile for new user");
                }
                //testCreateNotif();
                createNewNotifications(); //populate the new notifications to the device notifications
            }
        });
        // sets the currentUser attribute for MainActivity


    }

    /**
     * TODO remove this
     * Author: Erin-Marie
     * This is a method that is only for testing purposes as I figure out how to set up notifications
     * it just creates a test notification and sends it to the running device
     * It is called within setUpDB() so it is called everytime the app opens.
     */
    public void testCreateNotif(){
        ArrayList<DocumentReference> recipients = new ArrayList<DocumentReference>();
        recipients.add(user.getDocRef());

        Notification notif = new Notification("test notification2", "test message", recipients, user);
        notifDB.addNotification(notif);

        Log.v(TAG, "notif created");

    }

    /**
     * Author: Erin-Marie
     * This method initiates the users notification channel and the notificationManager
     */
    public void setUpNotifChannel(){
        // Create the users NotificationChannel
        CharSequence name = "My Notifications";
        String description = "Notifications channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{1000, 2000});

        // Register the channel with the system
        notificationManager.createNotificationChannel(channel);

        //get the users new notifications and send them to system
        //createNewNotifications();
    }

    /**
     * Author: Erin-Marie
     * method loops through the users list of new notifications and sends them to the device
     * This method is called after the users notifications has been read from the db
     */
    public void createNewNotifications(){
        //make all their new notifications populate
        ArrayList<Notification> newNotifications = notifDB.getMyNewNotifs();
        //assert !newNotifications.isEmpty();
        for (int i = 0; i < newNotifications.size(); i++) {
            displayNotification(newNotifications.get(i), i); //pass i to serve as the unique notification id (unique for this array)
        }
    }

    /**
     * Author: Erin-Marie
     * This method displays/sends a notification as a device notification
     * Reference: <a href="https://developer.android.com/develop/ui/views/notifications/build-notification">...</a>
     * TODO does it check that the user has allowed notifications?
     *
     * @param notification the notification instance to be displayed
     * @param id the index of the nortification from myNewNotifications
     */
    public void displayNotification(Notification notification, int id){
        //The pending intent makes it so that when the user selects the notification, it launches the app
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                //set the small icon to be the app logo
                .setSmallIcon(R.mipmap.app_logo)
                //Title will be either "Noodle Lottery Result from <Event Name>" or "<Message title> from <Sender name>"
                .setContentTitle(notification.getTitle() + " from " + notification.getSender())
                .setContentText(notification.getMessage())
                //So when the message is long you have to tap the notification to expand it
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getMessage()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(CHANNEL_ID)
                //set the app action+
                .setContentIntent(pendingIntent);

        this.notificationManager.notify(id, builder.build());
        Log.v(TAG, "notification sent");
    }

    // a method used for testing
    public UserProfile getUser(){
        return user;
    }

    public DBConnection getConnection(){
        return connection;
    }

}
