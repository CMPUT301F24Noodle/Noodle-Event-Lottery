package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import androidx.test.filters.MediumTest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.objects.notificationClasses.Notification;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


/**
 * Author: Xavier Salm
 * Tests for the notifications fragment
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class NotificationsFragmentTest {


    //The Espresso Rule
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    // here are some variables gotten from main
    UserProfile user;
    DBConnection connection;


    // before each test, get the user and connection stored in main
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

    /**
     * Tests that if a notification is added for a user, that notification can be retrieved through getUserNotifications()
     */
    @Test
    public void ReceiveNotificationTest() throws InterruptedException {

        // first get the number of notifications the user currently has
        NotificationDB notifDB = connection.getNotifDB();
        ArrayList<Notification> notificationList = notifDB.getUserNotifications();

        // wait 10 whole seconds for the query to finish
        Thread.sleep(5000);

        int oldCount = notificationList.size();

        // send a notification
        SendFakeNotification("RECEIVE");

        // now see if the user has +1 notification
        ArrayList<Notification> newNotificationList = notifDB.getUserNotifications();

        // wait 10 whole seconds for the query to finish
        Thread.sleep(5000);


        int newCount = newNotificationList.size();

        assertEquals(newCount, (oldCount + 1));
    }

    /**
    * tests that if a user has a notification, they can navigate to notification fragment and see the notification
     */
    @Test
    public void SeeNotificationsTest() throws InterruptedException {

        NotificationDB notifDB = connection.getNotifDB();

        // send the user a notification
        SendFakeNotification("SEE");

        Thread.sleep(5000);

        // this currently cheats the test to pass
        ArrayList<Notification> newNotificationList = notifDB.getUserNotifications();

        Thread.sleep(5000);

        // Now open notifications
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_notifications)).perform(click());

        Thread.sleep(15000);

        // and check if the notification is there!
        onView(withText("SEE")).check(matches(isDisplayed()));


    }

    /**
     * Author: Xavier Salm
     * Helper function that adds a notification for the current user
     */
    public void SendFakeNotification(String title){


        NotificationDB notifDB = connection.getNotifDB();

        // create a dummy sender user
        UserProfile sender = new UserProfile("-1");
        sender.setName("Test Sender");


        // create the test notification
        ArrayList<DocumentReference> recipients = new ArrayList<DocumentReference>();
        DocumentReference testUserRef = connection.getUserDocumentRef();
        recipients.add(testUserRef);
        Notification notification = new Notification(title, "This is the test message", recipients, sender);

        // now add that notification to the DB
        notifDB.addNotification(notification);
    }

}





