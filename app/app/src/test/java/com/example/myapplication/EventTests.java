package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.type.DateTime;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * tests for the Event class, and EventDBConnector
 */
public class EventTests {

    UserProfile organizer = new UserProfile();
    UserProfile entrant = new UserProfile();
    Facility facility = new Facility("TestFacility", organizer, "your moms house");

    /**
     * method makes an event object the test cases can use.
     * @throws ParseException if the string format of the date is wrong but it wont be because its hardcoded for these tests
     */
    public Event makeTestEvent() throws ParseException {
        Date dateEvent = new SimpleDateFormat("dd/mm/yyyy").parse("29/05/2002");
        Date dateClose = new SimpleDateFormat("dd/mm/yyyy").parse("30/05/2002");
        //event with default details, geolocation off, max participants is 10
        Event event = new Event(facility, organizer, "TestEvent", null, dateEvent, 1, dateClose, Boolean.FALSE);
        return event;
    }

    /**
     * this tests that the addEvents method does not add an event when the entrant max has been reached, but does if it has not been reached
     * and that addEvents is updating the eventFull attribute after an entrant is added, by calling setEventFull
     * @throws ParseException if the date string is the wrong format
     */
    @Test
    public void testAddEntrant() throws ParseException {
        //Make a new event
        Event event = makeTestEvent();

        //Test that you can add an entrant if the max is not reached
        Integer entrantCount = event.countEntrants();
        //first check that the event is empty
        assertEquals(entrantCount.longValue(), 0);
        //check that the eventFull attr is set to False
        assertEquals(event.getEventFull(), Boolean.FALSE);
        //add an entrant and ensure it is successful
        assertEquals(event.addEntrant(entrant), 1);
        //and check that they were added to the entrants list
        assertEquals(event.getEntrants().contains(entrant), Boolean.TRUE);
        //update entrantCount value

        //Test that you cannot add an entrant if the max is reached
        entrantCount = event.countEntrants();
        //now check that the event is at the max entrants of 1
        assertEquals(entrantCount.longValue(), event.getMaxEntrants().longValue());
        //now check that the event has been set to full
        assertEquals(event.getEventFull(), Boolean.TRUE);
        UserProfile newEntrant = new UserProfile();
        //now try to add a new entrant and ensure it fails
        assertEquals(event.addEntrant(newEntrant), 0);
        //and check that they were not added to the entrants list
        assertEquals(event.getEntrants().contains(newEntrant), Boolean.FALSE);

    }

}
