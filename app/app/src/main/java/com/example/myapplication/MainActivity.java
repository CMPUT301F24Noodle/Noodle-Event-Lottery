package com.example.myapplication;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

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

//        //Add the user to the db, added by Erin-Marie, if it breaks everything its my fault
//        this.connection = new DBConnection(getBaseContext());
//        this.userDB = new UserDB(this.connection);
//        this.uuid = connection.getUUID(); //store the users uuid
//        if (connection.checkUserDocExists() == Boolean.FALSE) {
//            this.user = new UserProfile(this.uuid); //make a new UserProfile
//            this.userDB.addUser(this.user); //add the new user to the db
//            //TESTME: write a test that confirms the user was just added
//        } else {
//            this.user = userDB.getCurrentUser();
//        }
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
     * gets the current users USerProfile object based on the data stored in the db
     * If the user is not in the db, it adds them to the db.
     */
    public void setUpDB(){
        //Add the user to the db, added by Erin-Marie, if it breaks everything its my fault
        this.connection = new DBConnection(this);
        this.userDB = new UserDB(this.connection);
        this.uuid = connection.getUUID(); //store the users uuid

        //BROKEN: this null case doesn't work, it is not checking if the user exists already and it overwrite the stored data
        if (this.userDB.getUserDocument() != null) {
            this.user = new UserProfile(this.uuid); //make a new UserProfile
            this.userDB.addUser(this.user); //add the new user to the db
            this.userDB = new UserDB(this.connection);
            this.user = this.connection.getDocumentSnapshot(this.userDB.getUserDocument());
            //TEST: this.userDB.userDocument.update("firstName", "Erin");

        }



        }
    }
