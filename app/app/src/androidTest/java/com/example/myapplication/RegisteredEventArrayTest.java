package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.ui.registeredevents.RegisteredEventArrayAdapter;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.database.EventDB;
import com.google.zxing.WriterException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Author: Sam Lee
 * Tests for the RegisteredEventArrayAdapter
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisteredEventArrayTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    private UserDB mockUserDB;
    private EventDB mockEventDB;
    private UserProfile mockUserProfile;

    // Set up mocks for the UserDB and EventDB
    @Before
    public void setUp() {
        mockUserDB = Mockito.mock(UserDB.class);
        mockEventDB = Mockito.mock(EventDB.class);
        mockUserProfile = new UserProfile();
        mockUserProfile.setName("Test User");

        when(mockUserDB.getCurrentUser()).thenReturn(mockUserProfile);
    }

    // Create a test event
    public static Event makeTestEvent() throws ParseException, WriterException {
        UserProfile organizer = new UserProfile();
        organizer.setName("Test Org");
        Facility facility = new Facility("TestFacility", "Testing area", organizer );
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateEvent = dateFormat.parse("29/05/2002");
        Date dateClose = dateFormat.parse("30/05/2002");
        return new Event(facility, organizer, "TestEvent", null, dateEvent, null, null, 1, 1, dateClose, Boolean.FALSE);
    }

    /**
     * Author: Sam Lee
     * Test that the RegisteredEventArrayAdapter displays the event details
     * correctly
     * 
     * @throws ParseException
     * @throws WriterException
     */
    @Test
    public void testRegisteredEventArrayAdapter() throws ParseException, WriterException {
        // Create a test event
        Event event = makeTestEvent();

        // Create a list of events and add the test event
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);

        // Create the adapter
        Context context = ApplicationProvider.getApplicationContext();
        RegisteredEventArrayAdapter adapter = new RegisteredEventArrayAdapter(context, events, mockUserDB, mockEventDB);

        // Get the view for the first item in the adapter
        View view = adapter.getView(0, null, null);

        // Verify that the event details are displayed correctly
        TextView eventName = view.findViewById(R.id.eventname_status);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView orgName = view.findViewById(R.id.organizer_name);

        assertEquals("TestEvent", eventName.getText().toString());
        // no date test
        assertEquals("No Time", eventTime.getText().toString());
        assertEquals("Test Org", orgName.getText().toString());
    }

    /**
     * Author: Sam Lee
     * Test that the RegisteredEventArrayAdapter correctly handles accepting an
     * invitation
     * 
     * @throws ParseException
     * @throws WriterException
     */
    @Test
    public void testAcceptInvitation() throws ParseException, WriterException {
        // Create a test event
        Event event = makeTestEvent();
        event.getWinnersList().add(mockUserDB.getCurrentUser().getDocRef());

        // Create a list of events and add the test event
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);

        // Create the adapter
        Context context = ApplicationProvider.getApplicationContext();
        RegisteredEventArrayAdapter adapter = new RegisteredEventArrayAdapter(context, events, mockUserDB, mockEventDB);

        // Simulate accepting the invitation
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> adapter.acceptInvitation(event));

        // Verify that the lists are updated correctly
        assertEquals(0, event.getWinnersList().size());
        assertEquals(1, event.getAcceptedList().size());
        assertEquals(0, event.getEntrantsList().size());
        assertEquals(mockUserDB.getCurrentUser().getDocRef(), event.getAcceptedList().get(0));
    }

    /**
     * Author: Sam Lee
     * Test that the RegisteredEventArrayAdapter correctly handles declining an
     * invitation
     * 
     * @throws ParseException
     * @throws WriterException
     */
    @Test
    public void testDeclineInvitation() throws ParseException, WriterException {
        // Create a test event
        Event event = makeTestEvent();
        event.getWinnersList().add(mockUserDB.getCurrentUser().getDocRef());

        // Create a list of events and add the test event
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);

        // Create the adapter
        Context context = ApplicationProvider.getApplicationContext();
        RegisteredEventArrayAdapter adapter = new RegisteredEventArrayAdapter(context, events, mockUserDB, mockEventDB);

        // Simulate declining the invitation
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> adapter.declineInvitation(event));

        // Verify that the lists are updated correctly
        assertEquals(0, event.getWinnersList().size());
        assertEquals(1, event.getDeclinedList().size());
        assertEquals(0, event.getEntrantsList().size());
        assertEquals(mockUserDB.getCurrentUser().getDocRef(), event.getDeclinedList().get(0));
    }
}