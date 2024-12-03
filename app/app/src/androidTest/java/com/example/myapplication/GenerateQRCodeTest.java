package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserProfile;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Includes the test for all userstory about creating an event
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class GenerateQRCodeTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    UserProfile user;
    DBConnection connection;
    UserDB userDB;

    // run this before every test to save repeating code
    @Before
    public void NavigateToMyProfileSetUp() throws InterruptedException {

        scenario.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                // call a few methods in MainActivity
                user = activity.getUser();
                connection = activity.getConnection();
                userDB = connection.getUserDB();
            }
        });

        Thread.sleep(1000);
        user = userDB.getCurrentUser();
        // Then clean the user so that they have the qualities of a default user
        user.setName("TestName");
        user.setEmail("TestEmail");
        user.setPhoneNumber(null);
        if(user.getFacility() != null){
            Facility facility = user.getFacility();
            user.removeFacility(facility);
        }
        UserDB userDB = connection.getUserDB();
        userDB.updateUserDocument(user);


        // FIRST MAKE USER AN ORGANIZER
        makeOrganizer();

        // Start by clicking on the side menu bar
        onView(withContentDescription("Open navigation drawer")).perform(click());

        // Now go to MyProfile
        // nav_profile is the nav button for MyProfile, the list of IDs is in activity_main_drawer.xml, which is under menu
        onView(withId(R.id.nav_myevents)).perform(click());

    }

    /**
     * US 02.01.02 As an organizer I want to store hash data of the generated QR code in my database
     * US 02.01.01 As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app
     * US 02.05.02 As an organizer I want to set the system to sample a specified number of attendees to register for the event
     * US 02.02.03 As an organizer I want to enable or disable the geolocation requirement for my event.
     *
     * @throws InterruptedException
     */
    @Test
    public void generateQRCodeTest() throws InterruptedException {
        makeEvent();

        Thread.sleep(5000); // wait for query

        onView(withId(R.id.generate_qr)).perform(click());

        Thread.sleep(1000);

        // hope and pray
        onView(withId(R.id.full_QR_image)).check(matches(isDisplayed()));
    }

    public void makeOrganizer(){
        // go to my profile
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());

        // make facility stuff visible
        onView(withId(R.id.profile_facility_toggle_switch)).perform(click());
        onView(withText("Continue")).perform(click());

        // type into both fields
        onView(withId(R.id.profile_facility_name)).perform(ViewActions.typeText("organized name"));
        onView(withId(R.id.profile_facility_location)).perform(ViewActions.typeText("organized location"));

        // save
        onView(withId(R.id.profile_save_info_button)).perform(click());
    }

    public void makeEvent() throws InterruptedException {
        // create event
        onView(withId(R.id.create_event_button)).perform(click());
        onView(withId(R.id.repeating_event_toggle)).perform(click());

        onView(withId(R.id.event_name_edit)).perform(ViewActions.typeText("TestEventName"));
        onView(withId(R.id.event_location_edit)).perform(ViewActions.typeText("TestEventLocation"));
        onView(withId(R.id.date_picker_DD)).perform(ViewActions.typeText("11"));
        onView(withId(R.id.date_picker_MM)).perform(ViewActions.typeText("11"));
        onView(withId(R.id.date_picker_YY)).perform(ViewActions.typeText("1111"));
        onView(withId(R.id.time_picker_hh)).perform(ViewActions.typeText("11"));
        onView(withId(R.id.time_picker_mm)).perform(ViewActions.typeText("11"));

        onView(withId(R.id.regstart_date_picker_DD)).perform(ViewActions.typeText("11"));
        onView(withId(R.id.regstart_date_picker_MM)).perform(ViewActions.typeText("11"));
        onView(withId(R.id.regstart_date_picker_YY)).perform(ViewActions.typeText("1111"));

        onView(withId(R.id.regend_date_picker_DD)).perform(ViewActions.typeText("11"));
        onView(withId(R.id.regend_date_picker_MM)).perform(ViewActions.typeText("11"));
        onView(withId(R.id.regend_date_picker_YY)).perform(ViewActions.typeText("1111"));


        //US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
        onView(withId(R.id.waiting_list_limit)).perform(ViewActions.typeText("20"));


        onView(withId(R.id.contact_num)).perform(ViewActions.typeText("1"));

        //US 02.05.02 As an organizer I want to set the system to sample a specified number of attendees to register for the event
        onView(withId(R.id.max_participants)).perform(ViewActions.typeText("10"));

        //US 02.02.03 As an organizer I want to enable or disable the geolocation requirement for my event.
        onView(withId(R.id.geolocation_toggle)).perform(click());



        onView(withText("Save")).perform(scrollTo()).perform(click());

        Thread.sleep(5000);
    }

}
