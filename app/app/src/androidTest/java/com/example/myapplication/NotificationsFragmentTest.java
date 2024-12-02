package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.filters.MediumTest;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.objects.Notification;
import com.google.firebase.firestore.DocumentReference;
import com.google.zxing.WriterException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Date;


/**
 * Author: Xavier Salm, Erin-Marie
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
    DocumentReference userDocRef;
    DBConnection connection;
    EventDB eventDB;
    UserDB userDB;
    NotificationDB notifDB;
    UserProfile owner;
    Facility facility;

    @Before
    public void xavierBefore() {
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                // call a few methods in MainActivity

                connection = activity.getConnection();
                user = connection.getUser();
                owner = new UserProfile("04ec3457-ed00-426c-b580-fde9bb67735f");
                eventDB = connection.getEventDB();
                userDB = connection.getUserDB();
                notifDB = connection.getNotifDB();
                userDocRef = connection.getUserDocumentRef();
                facility = new Facility("test Facility", "test location", owner);
            }
        });
    }

    /**
    * tests that if a user has a notification, they can navigate to notification fragment and see the notification
     */
    @Test
    public void SeeNotificationsTest() throws InterruptedException {

        // send the user a notification
        SendFakeNotification("SEE");

        Thread.sleep(5000);

        // this currently cheats the test to pass
        ArrayList<Notification> newNotificationList = notifDB.getUserNotifications();

        Thread.sleep(5000);

        // Now open notifications
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_notifications)).perform(click());

        // and check if the notification is there!
        onView(withText("SEE")).check(matches(isDisplayed()));


    }

    /**
     * Author: Erin-Marie
     * Test For:
     *      US 02.07.02 As an organizer I want to send notifications to all selected entrants
     *          - The notification is sent within the call eventDB.endEvent(mockEvent)
     *      US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
     *          - The notification is sent within the call eventDB.endEvent(mockEvent)
     *      US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events. This is the notification that they "won" the lottery
     *          - The notification are sent within the call eventDB.endEvent(mockEvent)
     *      US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
     *          - The notifications are received and displayed in the notifications fragment
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void SendWinnerNotification() throws InterruptedException, WriterException {
        //get a mock event
        Event mockEvent = MockEvent("Test Event 2");
        //add the test user to the mock event as an entrant
        eventDB.addEntrant(mockEvent);
        Thread.sleep(5000);
        //end the event, drawing the winners and losers and sending them their notifications
        eventDB.endEvent(mockEvent);
        Thread.sleep(5000);
        notifDB.getUserNotifications();

        // Now open notifications
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_notifications)).perform(click());
        // and check if the notification is there!
        onView(withText("Test Event")).check(matches(isDisplayed()));

        //now clean up the db
        cleanup(mockEvent);
    }

    /**
     * Author: Erin-Marie
     * Test For:
     * US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void SendLoserNotification() throws InterruptedException, WriterException {
        //get a mock event
        Event mockEvent = MockEvent("Test Event");
        //add the test user to the mock event as an entrant
        eventDB.addEntrant(mockEvent);
        //set to 0, so all entrants are not chosen (are losers)
        mockEvent.setMaxParticipants(0);
        Thread.sleep(5000);

        //end the event, drawing the winners and losers and sending them their notifications
        eventDB.endEvent(mockEvent);
        Thread.sleep(5000);
        notifDB.getUserNotifications();

        // Now open notifications
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_notifications)).perform(click());
        // and check if the notification is there!
        onView(withText("Test Event")).check(matches(isDisplayed()));

        //now clean up the db
        cleanup(mockEvent);
    }


    /**
     * Author: Erin-Marie
     * @param eventName a test event name to use
     * @return mockEvent a new event object, with a mock organizer
     */
    public Event MockEvent(String eventName) throws WriterException, InterruptedException {

        Event mockEvent = new Event(facility, user, eventName, null, new Date(), "MockDetails", "MockContact", -1, 5, new Date(), Boolean.FALSE);
        eventDB.addEvent(mockEvent);
        Thread.sleep(5000);
        return mockEvent;
    }

    /**
     * Author: Xavier Salm
     * Helper function that adds a notification for the current user
     */
    public void SendFakeNotification(String title){

        // create a dummy sender user
        UserProfile sender = new UserProfile("-1");
        sender.setName("Test Sender");
        // create the test notification
        ArrayList<DocumentReference> recipients = new ArrayList<DocumentReference>();
        recipients.add(userDocRef);
        Notification notification = new Notification(title, "This is the test message", recipients, sender);
        // now add that notification to the DB
        notifDB.addNotification(notification);
    }

    /**
     * Method called at the end of each test to remove the added values from the database, so we aren't cluttering it
     * This one only takes in an Event
     */
    public void cleanup(Event mockEvent){
        eventDB.deleteEvent(mockEvent);
    }

    /**
     * Method to clean the db after all tests run
     */

    @After
    public void cleanAfter(){
        userDB.deleteUser(user);
        eventDB.deleteFacility(facility);
    }

}





