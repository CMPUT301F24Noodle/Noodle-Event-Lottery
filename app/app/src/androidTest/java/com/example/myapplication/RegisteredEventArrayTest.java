package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.example.myapplication.R;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.ui.registeredevents.RegisteredEventArrayAdapter;
import com.google.zxing.WriterException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Author: Sam Lee
 *  Test for RegisteredEventArrayAdapter
 *  It is not completed.
 *  For some reason, I cannot test date and time formatting in this test
 *  It always fails due to time zone issues
 */
@RunWith(JUnit4.class)
public class RegisteredEventArrayTest {

    public static Event makeTestEvent() throws ParseException, WriterException {
        UserProfile organizer = new UserProfile();
        Facility facility = new Facility("TestFacility", "your moms house");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateEvent = dateFormat.parse("29/05/2002");
        Date dateClose = dateFormat.parse("30/05/2002");
        return new Event(facility, organizer, "TestEvent", null, dateEvent, 1, dateClose, Boolean.FALSE);
    }

    @Test
    public void testRegisteredEventArrayAdapterDisplaysEventDetails() throws ParseException, WriterException {
        // Create a test event
        Event event = makeTestEvent();

        // Create a list of events and add the test event
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);

        // Create the adapter
        Context context = ApplicationProvider.getApplicationContext();
        RegisteredEventArrayAdapter adapter = new RegisteredEventArrayAdapter(context, events);

        // Get the view for the first item in the adapter
        View view = adapter.getView(0, null, null);

        // Verify that the event details are displayed correctly
        TextView eventName = view.findViewById(R.id.eventname_status);
        TextView orgName = view.findViewById(R.id.organizer_name);

        assertEquals("TestEvent", eventName.getText().toString());
        assertEquals("No Org", orgName.getText().toString()); // Assuming organizer is null in the test event
    }
}