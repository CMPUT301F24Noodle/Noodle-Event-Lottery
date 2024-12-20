package com.example.myapplication;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Notification;
import com.example.myapplication.objects.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public Event currentEvent;
    public Event scannedEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // will get the current Users Profile, initialize db connections
        setUpDB();

        // Create the users NotificationChannel
        setUpNotifChannel();

        //Set the view and its attr
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        CircleImageView profileImage = headerView.findViewById(R.id.nav_header_profile_image);

        //TODO apoorv this is the code Erin adde to fix the menu bar delay
        updateSidebarHeader("Information Loading","Please Wait",null);
        Menu menu = navigationView.getMenu();
        MenuItem navORG = menu.findItem(R.id.nav_myevents);
        MenuItem navADM = menu.findItem(R.id.nav_admin);
        navORG.setVisible(true);
        navADM.setVisible(true);

        //configure the nav bar
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_myevents, R.id.nav_registered, R.id.nav_notifications, R.id.nav_metrics)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        profileImage.setOnClickListener(v -> {
            navController.navigate(R.id.nav_profile);
            drawer.closeDrawer(GravityCompat.START);// Replace with the correct ID of your ProfileFragment
        });

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
                BitmapHelper helper = new BitmapHelper();
                if (snapshot != null) { // user is already in the database
                    // fetch their profile data, make it a userprofile object, and store it as
                    // currentUser
                    // done through addUser() method in order to get the value returned from checkUserExists()
                    userDB.setCurrentProfile(snapshot);
                    user = userDB.getCurrentUser();
                    connection.setUser(user);
                    eventDB.getUserEnteredEvents(user); //initiate their list of entered events
                    eventDB.getUserOrgEvents(user); //initiate their list of organized events
                    notifDB.getUserNotifications(); //intitiate their list of all notifications
                    if (user.getAllowNotifs() == Boolean.TRUE) {
                        notifDB.getUserNewNotifications(); //get the list of users unseen notifications
                    }

                    Log.v("SetUpDB", "Set profile for existing user");
                    Bitmap pfp = helper.loadProfilePicture(user);
                    updateSidebarHeader(user.getName(),user.getEmail(),pfp);
                    updateSidebarForUserType(user);

                } else { // User is not already in the database
                    // create a new profile objet, and store it in the db
                    // done through addUser() method in order to get the value returned from checkUserExists()
                    userDB.addCurrentUser();
                    user = userDB.getCurrentUser();
                    connection.setUser(user);
                    eventDB.getUserEnteredEvents(user);
                    eventDB.getUserOrgEvents(user);

                    Log.v("SetUpDB", "Set profile for new user");

                    Bitmap pfp = helper.loadProfilePicture(user);
                    updateSidebarHeader("Try updating your profile data!","Welcome to Noodle!",pfp);
                    updateSidebarForUserType(user);
                }
                //testCreateNotif();
                createNewNotifications(); //populate the new notifications to the device notifications


            }
        });
        // sets the currentUser attribute for MainActivity


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
    /**
     * Author: Apoorv
     * This method updates the sidebar header to show accurate profile information
     * @param name the name set by the user
     * @param email the email of the user
     * @param profileImage the bitmap of the profile image set by the user.
     */
    public void updateSidebarHeader(String name, String email, Bitmap profileImage) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);


        TextView headerName = headerView.findViewById(R.id.nav_header_username);
        if (name!= null){
            headerName.setText(name);
        } else{
            headerName.setText("Try updating your profile data!");
        }

        // Update email
        TextView headerEmail = headerView.findViewById(R.id.nav_header_email_address);
        if (email!= null){
            headerEmail.setText(email);
        } else{
            headerEmail.setText("Welcome to Noodle!");
        }


        // Update profile image
        CircleImageView headerProfileImage = headerView.findViewById(R.id.nav_header_profile_image);
        headerProfileImage.setImageBitmap(profileImage);
    }
    /**
     * Author: Apoorv
     * This method updates the sidebar to show menus that the user has access to.
     * @param user the user who's privileges are being checked.
     */
    public void updateSidebarForUserType(UserProfile user) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem navORG = menu.findItem(R.id.nav_myevents);
        MenuItem navADM = menu.findItem(R.id.nav_admin);
        //TODO this is for demo only
//        navORG.setVisible(user.getPrivileges() == 1);
//        navADM.setVisible(user.getAdmin());
    }

    // a method used for testing
    public UserProfile getUser(){
        return user;
    }

    public DBConnection getConnection(){
        return connection;
    }


}
