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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.test.InstrumentationRegistry;
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Author: Erin-Marie
 * Tests for the notifications fragment
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class NotificationsFragmentTest {


    //The Espresso Rule
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    //public IntentsRule intentsTestRule = new IntentsRule();

    //private final Activity testActivity = new Activity();
    private final Intent testIntent = new Intent();
//
//    @Before
//    public void setup(){
//       // ActivityScenario<MainActivity> activityScenario = activityRule.getScenario();
//
//    }

//    @Before
//    public void intent(){
//        Intent intent = new Intent();
//        activityRule.launch();
//    }

//    @Test
//    public void NotificationScreenDisplayed() throws InterruptedException {
//        //testActivity.recreate();
//
//
//        onView(withId(R.id.nav_notifications)).perform(click());
//        onView(withId(R.id.notifications_list)).check(matches(isDisplayed()));
//    }

    /**
     * Author: Erin-Marie
     * Test for navigating to the Notifications Fragment
     */
    @Test
    public void testOpenNotifications() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click the Notifications menu item
        onView(withText("Notifications"))
                .perform(click());

        // Verify that we have really clicked on the icon by checking the TextView content, and ensuring it matches the desired layout
        onView(withId(R.id.frag_notifications))
                .check(matches(isDisplayed()));
    }







}
