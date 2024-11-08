package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.MockDBConnection;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.ui.notifications.Notification;
import com.example.myapplication.ui.notifications.NotificationsFragment;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import java.util.ArrayList;


/**
 * Author: Erin-Marie
 * Tests for the notifications fragment
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class NotificationsFragmentTest {


    //The Espresso Rule
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    //public IntentsRule intentsTestRule = new IntentsRule();

    //private final Activity testActivity = new Activity();
    private final Intent testIntent = new Intent();

    // here are some variables gotten from main
    UserProfile user;
    DBConnection connection;


    // get some stuff from main to use in here
    @Before
    public void xavierBefore() {
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                // call a few methods in MainActivity
                user = activity.getUser();
                connection = activity.getConnection();
            }
        });
    }

    // this is more of a unit test than a UI test
    @Test
    public void ReceiveNotificationTest() throws InterruptedException {

        // first get the number of notifications the user currently has
        NotificationDB notifDB = connection.getNotifDB();
        ArrayList<Notification> notificationList = notifDB.getUserNotifications();

        // wait 10 whole seconds for the query to finish
        Thread.sleep(5000);

        int currentCount = notificationList.size();

        // create a dummy sender user
        UserProfile sender = new UserProfile("-1");
        sender.setName("Test Sender");


        // create the test notification
        ArrayList<DocumentReference> recipients = new ArrayList<DocumentReference>();
        DocumentReference testUserRef = connection.getUserDocumentRef();
        recipients.add(testUserRef);
        Notification notification = new Notification("Test notification", "This is the testing message", recipients, sender);

        // now add that notification to the DB
        notifDB.addNotification(notification);

        // now see if the user has +1 notification
        ArrayList<Notification> newNotificationList = notifDB.getUserNotifications();

        // wait 10 whole seconds for the query to finish
        Thread.sleep(5000);

        int newCount = notificationList.size();

        assertEquals(newCount, (currentCount + 1));
    }

    @Test
    public void SeeNotificationsTest() throws InterruptedException {

        // get notif db
        NotificationDB notifDB = connection.getNotifDB();

        // create a dummy sender user
        UserProfile sender = new UserProfile("-1");
        sender.setName("Test Sender");

        // create the test notification
        ArrayList<DocumentReference> recipients = new ArrayList<DocumentReference>();
        DocumentReference testUserRef = connection.getUserDocumentRef();
        recipients.add(testUserRef);
        Notification notification = new Notification("BIG Test notification", "This is the testing message", recipients, sender);

        // now add that notification to the DB
        notifDB.addNotification(notification);

        Thread.sleep(5000);

        // Now open notifications
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_notifications)).perform(click());

        // and check if the notification is there!
        onView(withText("BIG Test notification")).check(matches(isDisplayed()));




    }


}





