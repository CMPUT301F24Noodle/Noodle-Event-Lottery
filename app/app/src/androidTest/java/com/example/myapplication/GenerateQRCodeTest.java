package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GenerateQRCodeTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    UserProfile user;
    DBConnection connection;

    // run this before every test to save repeating code
    @Before
    public void NavigateToMyProfileSetUp() throws InterruptedException {

        scenario.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                // call a few methods in MainActivity
                user = activity.getUser();
                connection = activity.getConnection();
            }
        });

        // Then clean the user so that they have the qualities of a default user
        user.setName("Name");
        user.setAddress("Email");
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

    @Test
    public void generateQRCodeTest() throws InterruptedException {
        // create event
        onView(withId(R.id.create_event_button)).perform(click());

        onView(withId(R.id.event_name_edit)).perform(ViewActions.typeText("event name"));
        onView(withId(R.id.event_location_edit)).perform(ViewActions.typeText("event location"));
        onView(withId(R.id.date_picker_DD)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.date_picker_MM)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.date_picker_YY)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.time_picker_hh)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.time_picker_mm)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.waiting_list_limit)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.contact_num)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.max_participants)).perform(ViewActions.typeText("10"));

        onView(withId(R.id.save_button)).perform(click());

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


}
