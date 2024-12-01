package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.ui.registeredevents.ViewScannedEventFragment;
import com.google.zxing.WriterException;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Author: Sam Lee
 * Tests for the ViewEventActivity
 */
@RunWith(AndroidJUnit4.class)
public class ViewScannedEventFragmentTest {

    public static Event makeTestEvent() throws ParseException, WriterException {
        UserProfile organizer = new UserProfile();
        Facility facility = new Facility("TestFacility", "your moms house", organizer);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateEvent = dateFormat.parse("29/05/2002");
        Date dateClose = dateFormat.parse("30/05/2002");
        return new Event(facility, organizer, "TestEvent", null, dateEvent, null, null, 1, 1, dateClose, Boolean.FALSE);
    }
//
//    /**
//     * Author: Sam Lee
//     * Tests that the event name is displayed correctly in the ViewEventActivity
//     *
//     * @throws ParseException
//     * @throws WriterException
//     */
//    @Test
//    public void testViewEventActivityDisplaysEventName() throws ParseException, WriterException {
//        // Create a test event
//        Event event = makeTestEvent();
//
//        // Launch ViewEventActivity with the test event
//        Intent intent = new Intent("com.example.myapplication.ACTION_VIEW_EVENT");
//        intent.putExtra("event", event);
//        try (ActivityScenario<ViewScannedEventFragment> scenario = ActivityScenario.launch(intent)) {
//            scenario.onActivity(activity -> {
//                // Verify that the event name is displayed correctly
//                TextView eventName = activity.findViewById(R.id.event_name);
//                assertEquals("TestEvent", eventName.getText().toString());
//            });
//        }
//    }
}
