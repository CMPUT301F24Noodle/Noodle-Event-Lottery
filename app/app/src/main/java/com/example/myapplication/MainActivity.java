package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public DBConnection connection;
    public UserDB userDB; //userDB instance for the current user
    public String uuid;
    public UserProfile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //will get the current Users Profile, initialize db connections
        setUpDB();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.qr_button).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //TODO: add UserProfile to the nav drawer so it can be selected and we can view the UserProfile fragment
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_myevents, R.id.nav_registered)
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
    public void setUpDB(){
        //Add the user to the db, added by Erin-Marie, if it breaks everything its my fault
        this.connection = new DBConnection(this); //connection to the base db
        this.userDB = new UserDB(this.connection); //the current users collection reference
        this.uuid = connection.getUUID(); //store the current users uuid
        //check if the user is already in the db
        this.userDB.checkUserExists(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot != null){  //user is already in the database
                    //fetch their profile data, make it a userprofile object, and store it as currentUser
                    //done through addUser() method in order to get the value returned from checkUserExists()
                    userDB.setCurrentProfile(snapshot);
                    Log.v("SetUpDB", "Set profile for existing user");

                } else { //User is not already in the database
                    //create a new profile objet, and store it in the db
                    //done through addUser() method in order to get the value returned from checkUserExists()
                    userDB.addCurrentUser();
                    Log.v("SetUpDB", "Set profile for new user");
                }
            }
        });
        //sets the currentUser attribute for MainActivity
        this.user = this.userDB.getCurrentUser();
        //TESTME: omg this fucking worked it fetched the profile without creating a new one or overwriting it
        //Task<Void> update = this.userDB.getUserDocument().update("firstName", "Erin");


    }
}
