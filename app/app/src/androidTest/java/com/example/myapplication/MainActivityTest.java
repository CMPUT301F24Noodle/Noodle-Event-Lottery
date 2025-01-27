package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.filters.MediumTest;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


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
