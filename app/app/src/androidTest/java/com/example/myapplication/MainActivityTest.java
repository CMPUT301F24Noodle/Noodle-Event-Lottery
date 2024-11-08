package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.database.MockDBConnection;
import com.example.myapplication.database.MockMainActivity;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.facilityClasses.Facility;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.zxing.WriterException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Author: Erin-Marie
 * Tests for the UI navigation from MainActivity
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class MainActivityTest {

    //The Espresso Rule
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);


    /**
     * Author: Erin-Marie
     * Test for navigating to the Notifications Fragment
     */
    @Test
    public void testOpenNotifications() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click the Notifications menu item
        onView(withText("Notifications")).perform(click());

        // Verify that we have really clicked on the icon by checking the TextView content, and ensuring it matches the desired layout
        onView(withId(R.id.frag_notifications)).check(matches(isDisplayed()));
    }

    /**
     * Author: Erin-Marie
     * Test for navigating to the My Entered Events Fragment
     */
    @Test
    public void testOpenMyEnteredEvents() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click the Entered Events menu item
        onView(withText("My Entered Events"))
                .perform(click());

        // Verify that we have really clicked on the icon by checking the TextView content, and ensuring it matches the desired layout
        onView(withId(R.id.my_entered_events))
                .check(matches(isDisplayed()));
    }


    /**
     * Author: Erin-Marie
     * Test for navigating to the My Organized Events Fragment
     */
    @Test
    public void testOpenMyOrgEvents() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click the Organized Events menu item
        onView(withText("My Organized Events"))
                .perform(click());

        // Verify that we have really clicked on the icon by checking the TextView content, and ensuring it matches the desired layout
        onView(withId(R.id.my_organized_events))
                .check(matches(isDisplayed()));
    }

    /**
     * Author: Erin-Marie
     * Test for navigating to the My Profile Fragment
     */
    @Test
    public void testOpenMyProfile() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click the Profile menu item
        onView(withText("Your Profile"))
                .perform(click());

        // Verify that we have really clicked on the icon by checking the TextView content, and ensuring it matches the desired layout

            onView(withId(R.id.my_profile))
                    .check(matches(isDisplayed()));

    }

    /**
     * Author: Erin-Marie
     * Test for navigating to the Admin Fragment
     * Test cannot be run until Admin fragment is implemented in Project part 4
     */
    //@Test
    public void testOpenAdmin() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click the Admin menu item
        onView(withText("App Management"))
                .perform(click());

        // Verify that we have really clicked on the icon by checking the TextView content, and ensuring it matches the desired layout
        onView(withId(R.id.admin)).check(matches(isDisplayed()));


    }












}
