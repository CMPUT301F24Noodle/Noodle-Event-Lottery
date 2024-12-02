package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserProfile;
import com.google.firebase.firestore.DocumentReference;
import com.google.zxing.WriterException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;


/**
 * Author: Erin-Marie
 * Tests for the manage event fragment
 * TODO: make them work
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class AdminFragmentTests {
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
    Facility facility;


    @Before
    public void Before() {
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                // call a few methods in MainActivity
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                connection = activity.getConnection();
                userDB = connection.getUserDB();
                user = userDB.getCurrentUser();
                eventDB = connection.getEventDB();
                notifDB = connection.getNotifDB();
                userDocRef = connection.getUserDocumentRef();
            }
        });
    }

    /**
     * Author: Erin-Marie
     * @param eventName a test event name to use
     * @return mockEvent a new event object, with a mock organizer
     */
    public Event MockEvent(String eventName) throws WriterException, InterruptedException {
        Thread.sleep(5000);

        user = userDB.getCurrentUser();
        user.setIsAdmin(Boolean.TRUE);
        facility = new Facility("test Facility", "test location", user);
        Event mockEvent = new Event(facility, user, eventName, null, new Date(), "MockDetails", "MockContact", -1, 5, new Date(), Boolean.FALSE);
        eventDB.addEvent(mockEvent);

        Thread.sleep(5000);
        eventDB.getUserOrgEvents(user);

        Thread.sleep(5000);
        return mockEvent;
    }

    /**
     * US.03.04.01 As an administrator, I want to be able to browse events
     * US 03.01.01 As an administrator, I want to be able to remove events
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void DeleteEventTest() throws InterruptedException, WriterException {
        Event event = MockEvent("DeleteThisEvent");

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("App Management")).perform(click());
        onView(withId(R.id.nav_admin_event)).perform(click());
        onView(withText("DeleteThisEvent")).perform(click());
        onView(withText("Remove")).perform(click());


    }
    
}
