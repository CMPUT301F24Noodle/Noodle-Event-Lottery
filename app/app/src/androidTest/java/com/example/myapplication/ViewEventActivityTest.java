package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.ui.ViewEventActivity;
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

@RunWith(AndroidJUnit4.class)
public class ViewEventActivityTest {

    public static Event makeTestEvent() throws ParseException, WriterException {
        UserProfile organizer = new UserProfile();
        Facility facility = new Facility("TestFacility", organizer, "your moms house");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateEvent = dateFormat.parse("29/05/2002");
        Date dateClose = dateFormat.parse("30/05/2002");
        return new Event(facility, organizer, "TestEvent", null, dateEvent, 1, dateClose, Boolean.FALSE);
    }

    @Test
    public void testViewEventActivityDisplaysEventName() throws ParseException, WriterException {
        // Create a test event
        Event event = makeTestEvent();

        // Launch ViewEventActivity with the test event
        Intent intent = new Intent("com.example.myapplication.ACTION_VIEW_EVENT");
        intent.putExtra("event", event);
        try (ActivityScenario<ViewEventActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Verify that the event name is displayed correctly
                TextView eventName = activity.findViewById(R.id.event_name);
                assertEquals("TestEvent", eventName.getText().toString());
            });
        }
    }
}

