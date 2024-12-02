package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
import java.util.ArrayList;
import java.util.Date;

/**
 * Author: Erin-Marie
 * Tests for the manage event fragment
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class ManageEventFragmentTest {
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
     * Creates a mockEvent, sets the User to be a winner of the lottery,
     * navigates the UI to remove the user from the winners list,
     * asserts that the winners list is now empty
     * US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void TestCancelEntrant() throws InterruptedException, WriterException {
        Thread.sleep(2000);
        //get a mock event
        Event mockEvent = MockEvent("Test Event5");
        //add the test user to the mock event as an entrant
        eventDB.addEntrant(mockEvent);
        //set to 0, so all entrants are not chosen (are losers)
        Thread.sleep(2000);
        //end the event, drawing the winners and losers and sending them their notifications
        eventDB.endEvent(mockEvent);
        Thread.sleep(2000);
        eventDB.getEventWinners(mockEvent);

        // Now open manage event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("My Organized Events")).perform(click());
        onView(withText("Test Event5")).perform(click());
        onView(withText("Manage Event")).perform(click());
        onView(withText("Winners")).perform(click());
        onView(withText("Anonymous_Noodle")).perform(click());
        Thread.sleep(100);
        onView(withText("Remove")).perform(click());
        Thread.sleep(100);
        eventDB.getEventWinners(mockEvent);

        assert (eventDB.getWinnersList().isEmpty());

    }

    /**
     * Author: Erin-Marie
     * Test to show that after an organizer has removed a user from the event, the user receives a notification informing them
     *  US 02.07.03 As an organizer I want to send a notification to all cancelled entrants
     * @throws InterruptedException
     */
    @Test
    public void TestRemovedNotification() throws InterruptedException {
        //Now show that they received a notification (since we have the entrant and organizer as the same user)
        notifDB.getUserNotifications();
        Thread.sleep(1000);
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_notifications)).perform(click());
        onView(withText("Test Event5")).check(matches(isDisplayed()));
    }

    /**
     * Author: Erin-Marie
     *  Test that the list of cancelled entrants is shown on the manage event page
     *  *  US 02.02.01 As an organizer I want to view the list of entrants who joined my event waiting list
     */
    @Test
    public void TestEntrantsListDisplayed() throws InterruptedException, WriterException {
        Event mockEvent = MockEvent("Test Event");
        ArrayList<UserProfile> listData = new ArrayList<>();
        listData.add(user);

        eventDB.addEntrant(mockEvent);
        Thread.sleep(100);
        eventDB.getEventEntrants(mockEvent);
        Thread.sleep(100);

        // Now open manage event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("My Organized Events")).perform(click());
        onView(withText("Test Event")).perform(click());
        onView(withText("Manage Event")).perform(click());
        onView(withText("Entrants")).perform(click());
        onView(withText("Anonymous_Noodle")).check(matches(isDisplayed()));
    }

    /**
     * Author: Erin-Marie
     * Test that the list of selected entrants is shown on the manage event page
     * US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply (the winners who have been offered an invitation)
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void TestWinnersListDisplayed() throws InterruptedException, WriterException {
        Event mockEvent = MockEvent("Test Event2");
        ArrayList<DocumentReference> listData = new ArrayList<>();
        listData.add(user.getDocRef());

        eventDB.endEvent(mockEvent);
        mockEvent.setWinnersList(listData);
        eventDB.updateEvent(mockEvent);
        Thread.sleep(100);
        eventDB.getEventWinners(mockEvent);
        Thread.sleep(100);

        // Now open manage event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("My Organized Events")).perform(click());
        onView(withText("Test Event2")).perform(click());
        onView(withText("Manage Event")).perform(click());
        onView(withText("Winners")).perform(click());
        onView(withText("Anonymous_Noodle")).check(matches(isDisplayed()));

    }

    /**
     * Author: Erin-Marie
     * Test that the list of cancelled entrants is shown on the manage event page
     * US 02.06.03 As an organizer I want to see a final list of entrants who enrolled for the event (Users that have accepted their invitation)
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void TestAcceptedListDisplayed() throws InterruptedException, WriterException {
        Event mockEvent = MockEvent("Test Event3");
        ArrayList<DocumentReference> listData = new ArrayList<>();
        listData.add(userDocRef);

        eventDB.endEvent(mockEvent);
        mockEvent.setAcceptedList(listData);
        eventDB.updateEvent(mockEvent);
        Thread.sleep(100);
        eventDB.getEventAccepted(mockEvent);
        Thread.sleep(100);

        // Now open manage event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("My Organized Events")).perform(click());
        onView(withText("Test Event3")).perform(click());
        onView(withText("Manage Event")).perform(click());
        onView(withText("Attending")).perform(click());
        onView(withText("Anonymous_Noodle")).check(matches(isDisplayed()));

    }

    /**
     * Author: Erin-Marie
     * Test that the list of declined entrants is shown on the manage event page
     * US 02.06.02 As an organizer I want to see a list of all the cancelled entrants
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void TestDeclinedListDisplayed() throws InterruptedException, WriterException {
        Event mockEvent = MockEvent("Test Event4");
        ArrayList<DocumentReference> listData = new ArrayList<>();
        listData.add(user.getDocRef());

        eventDB.endEvent(mockEvent);
        mockEvent.setDeclinedList(listData);
        eventDB.updateEvent(mockEvent);
        Thread.sleep(100);
        eventDB.getEventDeclined(mockEvent);
        Thread.sleep(100);

        // Now open manage event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("My Organized Events")).perform(click());
        onView(withText("Test Event4")).perform(click());
        onView(withText("Manage Event")).perform(click());
        onView(withText("Cancelled")).perform(click());
        onView(withText("Anonymous_Noodle")).check(matches(isDisplayed()));

    }

    /**
     * Tests for selecting a new winner
     *  *  US 02.05.03 As an organizer I want to be able to draw a replacement applicant from the pooling system when a previously selected applicant cancels or rejects the invitation
     *  *  US 01.05.01 As an entrant I want another chance to be chosen from the waiting list if a selected user declines an invitation to sign up
     * @throws InterruptedException
     * @throws WriterException
     */
    @Test
    public void TestReplaceEntrant() throws InterruptedException, WriterException {
        Event mockEvent = MockEvent("Test Event6");
        ArrayList<DocumentReference> listData = new ArrayList<>();
        listData.add(user.getDocRef());
        eventDB.endEvent(mockEvent);
        //a user in the loser list will be selected to fill the event
        mockEvent.setLosersList(listData);
        mockEvent.setEventOver(Boolean.TRUE);
        eventDB.updateEvent(mockEvent);
        Thread.sleep(5000);
        eventDB.getUserOrgEvents(user);
        Thread.sleep(5000);

        //Show that before executing the replace, there are no winners
        assert eventDB.getWinnersList().isEmpty();

        // Now open manage event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("My Organized Events")).perform(click());
        onView(withText("Test Event6")).perform(click());
        onView(withText("Manage Event")).perform(click());
        onView(withText("Replace Cancelled")).perform(click());
        Thread.sleep(1000);

        //show that there is now a new winner
        assert !eventDB.getWinnersList().isEmpty();
    }

    /**
     * Author: Erin-Marie
     * @param eventName a test event name to use
     * @return mockEvent a new event object, with a mock organizer
     */
    public Event MockEvent(String eventName) throws WriterException, InterruptedException {
        Thread.sleep(5000);

        user = userDB.getCurrentUser();
        facility = new Facility("test Facility", "test location", user);
        Event mockEvent = new Event(facility, user, eventName, null, new Date(), "MockDetails", "MockContact", -1, 5, new Date(), Boolean.FALSE);
        eventDB.addEvent(mockEvent);

        Thread.sleep(5000);
        eventDB.getUserOrgEvents(user);

        Thread.sleep(5000);
        return mockEvent;
    }

    /**
     * Method to clean the db after all tests run
     */

//    @After
//    public void cleanAfter(){
//        userDB.deleteUser(user);
//
//    }

}
