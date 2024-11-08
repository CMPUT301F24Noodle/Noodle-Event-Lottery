package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.provider.Settings;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import androidx.test.platform.app.InstrumentationRegistry;
import android.provider.Settings;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

/**
 * Author: Xavier Salm
 * Test class for all UI elements in MyProfileFragment
 * Run at your own risk! This test will override the info (user details, facility and facility details) of the user associated with the device that runs this test
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyProfileFragmentTest {
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

        // Start by clicking on the side menu bar
        onView(withContentDescription("Open navigation drawer")).perform(click());

        // Now go to MyProfile
        // nav_profile is the nav button for MyProfile, the list of IDs is in activity_main_drawer.xml, which is under menu
        onView(withId(R.id.nav_profile)).perform(click());


    }



    // Tests if you can actually navigate to MyProfile
    /**
     * Tests the ability to navigate to MyProfileFragment
     */
    @Test
    public void NavigateToMyProfileTest() {
        onView(withId(R.id.profile_facility_section_text)).check(matches(isDisplayed()));
    }


    /**
     * Tests if the user's details are correctly displayed
     */
    @Test
    public void DisplayUserInfoTest(){

        // check if text matches
        onView(withId(R.id.profile_user_name)).check(matches(withText("Name")));
        onView(withId(R.id.profile_user_email)).check(matches(withText("Email")));
    }

    /**
     * Tests the functionality of the save button. When clicked, most blank fields are reset to what they were before (they are not saved)
     * Tests user stories: US.01.02.01, US.01.02.02
     */
    @Test
    public void SaveUserInfoButtonTest(){

        // type in a number, and then give empty string for user name
        onView(withId(R.id.profile_user_contact_number)).perform(ViewActions.typeText("123456789"));
        onView(withId(R.id.profile_user_name)).perform(replaceText(""));

        // press save
        onView(withId(R.id.profile_save_info_button)).perform(click());

        // check if the expected values are in the text fields
        onView(withId(R.id.profile_user_name)).check(matches(withText("Name")));
        onView(withId(R.id.profile_user_contact_number)).check(matches(withText("123456789")));
    }

    /**
     * Tests that facility text fields are hidden while the toggle is off, and visible while the toggle is on
     */
    @Test
    public void FacilityVisibilityToggleTest(){
        // check that you can't see facility stuff
        onView(withId(R.id.profile_facility_name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // toggle on
        onView(withId(R.id.profile_facility_toggle_switch)).perform(click());

        // close the dialog popup
        onView(withText("Continue")).perform(click());

        // check that the views are now visible
        onView(withId(R.id.profile_facility_name)).check(matches(isDisplayed()));

        // toggle off
        onView(withId(R.id.profile_facility_toggle_switch)).perform(click());

        // check that you can't see facility stuff
        onView(withId(R.id.profile_facility_name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    /**
     * Tests the creation of facilities, which are only created for new users if both facility text fields are filled out
     * Tests User Story: US.02.01.03
     */
    @Test
    public void createFacilityTest() {

        // make facility stuff visible
        onView(withId(R.id.profile_facility_toggle_switch)).perform(click());
        onView(withText("Continue")).perform(click());

        // type into one field
        onView(withId(R.id.profile_facility_name)).perform(ViewActions.typeText("facility name"));

        // try to save
        onView(withId(R.id.profile_save_info_button)).perform(click());

        // it shouldn't work
        onView(withId(R.id.profile_facility_name)).check(matches(withText("")));

        // type into both fields
        onView(withId(R.id.profile_facility_name)).perform(ViewActions.typeText("name"));
        onView(withId(R.id.profile_facility_location)).perform(ViewActions.typeText("location"));

        // try to save again
        onView(withId(R.id.profile_save_info_button)).perform(click());

        // now it should stick
        onView(withId(R.id.profile_facility_name)).check(matches(withText("name")));
        onView(withId(R.id.profile_facility_location)).check(matches(withText("location")));


    }

    /**
     * Tests if the delete facility button clears the facility fields
     * Tests User Story: US.02.01.03
     */
    @Test
    public void DeleteFacilityButtonTest(){
        // make facility stuff visible
        onView(withId(R.id.profile_facility_toggle_switch)).perform(click());
        onView(withText("Continue")).perform(click());

        // type into both fields
        onView(withId(R.id.profile_facility_name)).perform(ViewActions.typeText("name"));
        onView(withId(R.id.profile_facility_location)).perform(ViewActions.typeText("location"));

        // save the facility
        onView(withId(R.id.profile_save_info_button)).perform(click());

        // delete facility
        onView(withId(R.id.profile_delete_facility_button)).perform(click());
        onView(withText("DELETE")).perform(click());

        // both fields should be empty
        onView(withId(R.id.profile_facility_name)).check(matches(withText("")));
        onView(withId(R.id.profile_facility_location)).check(matches(withText("")));

    }
}
