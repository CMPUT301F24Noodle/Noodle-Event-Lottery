package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserProfile;

/**
 * Author: Xavier Salm
 * Test class for all UI elements in MyProfileFragment
 *TODO:
 * US 01.03.02 As an entrant I want remove profile picture if need be
 * US 01.03.01 As an entrant I want to upload a profile picture for a more personalized experience
 * US 01.03.03 As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet
 * US 01.07.01 As an entrant, I want to be identified by my device, so that I don't have to use a username and password
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyProfileFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    UserProfile user;
    UserDB userDB;
    DBConnection connection;

    // run this before every test to save repeating code
    @Before
    public void NavigateToMyProfileSetUp() throws InterruptedException {

        scenario.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                // call a few methods in MainActivity
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


        userDB.updateUserDocument(user);
        Thread.sleep(1000);

        // Start by clicking on the side menu bar
        onView(withContentDescription("Open navigation drawer")).perform(click());

        // Now go to MyProfile
        // nav_profile is the nav button for MyProfile, the list of IDs is in activity_main_drawer.xml, which is under menu
        onView(withId(R.id.nav_profile)).perform(click());


    }


    /**
     * Tests if the user's details are correctly displayed
     */
    @Test
    public void DisplayUserInfoTest(){

        // check if text matches
        onView(withId(R.id.profile_user_name)).check(matches(withText("TestName")));
        onView(withId(R.id.profile_user_email)).check(matches(withText("TestEmail")));
    }

    /**
     * Tests the functionality of the save button. When clicked, most blank fields are reset to what they were before (they are not saved)
     * US 01.02.02 As an entrant I want to update information such as name, email and contact information on my profile
     * US 01.02.01 As an entrant, I want to provide my personal information such as name, email and optional phone number in the app
     */
    @Test
    public void SaveUserInfoButtonTest(){

        // type in a number, and then give empty string for user name
        onView(withId(R.id.profile_user_contact_number)).perform(ViewActions.typeText("123456789"));

        // press save
        onView(withId(R.id.profile_save_info_button)).perform(click());

        // check if the expected values are in the text fields
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
     * US 02.01.03 As an organizer, I want to create and manage my facility profile.
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

    /**
     * Tests if the toggle for notifications works
     * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
     */
    @Test
    public void ToggleNotificationsOffTest(){
        // toggle off notifications
        onView(withId(R.id.switch_notifications)).perform(click());
        //check that notifications attr in db is now set to off
        user = userDB.getCurrentUser();
        assert (user.getAllowNotifs() == Boolean.FALSE);
    }
}
