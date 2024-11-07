package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.provider.Settings;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.platform.app.InstrumentationRegistry;
import android.provider.Settings;

/**
 * Author: Xavier Salm
 * Test class for all UI elements in MyProfileFragment
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyProfileFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);


    // TODO DO TESTS NEED JAVADOCS?
    // Tests if you can actually navigate to MyProfile
    @Test
    public void NavigateToMyProfileTest() {

        // Start by clicking on the side menu bar
        // so the actual hamburger icon is not defined in xml, it is defined at the start of main_activity
        // this means it can't be accessed using an ID
        // however, the automatic process of setting up the hamburger icon gives it the content description of "Open navigation drawer"
        // so this is how you click it!!
        onView(withContentDescription("Open navigation drawer")).perform(click());

        // Now go to MyProfile
        // nav_profile is the nav button for MyProfile, the list of IDs is in activity_main_drawer.xml, which is under menu
        onView(withId(R.id.nav_profile)).perform(click());

        onView(withId(R.id.profile_facility_section_text)).check(matches(isDisplayed()));
    }

    // Test if the user's details are correctly displayed
    @Test
    public void DisplayUserInfoTest(){
        // navigate to my profile
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.profile_facility_section_text)).check(matches(isDisplayed()));

        // check if text matches

    }

}
