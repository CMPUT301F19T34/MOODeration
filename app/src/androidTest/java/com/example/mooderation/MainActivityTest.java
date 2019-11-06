package com.example.mooderation;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {
    private Solo solo;
    private UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(
            MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void testAddMoodEvent() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Happy"));
    }

    @Test
    public void testToggleLocation() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));


        // Test denying location permission
        solo.clickOnView(solo.getView(R.id.location_switch));

        // Initialize UiDevice instance
        UiObject denyButton = device.findObject(new UiSelector().text("Deny"));

        if(denyButton.waitForExists(500)) {
            try {
                denyButton.click();
                assertEquals(false, solo.isTextChecked("Attach location"));
            } catch (UiObjectNotFoundException e) {

            }
        }

        // Test allowing location permission from off-state
        solo.clickOnView(solo.getView(R.id.location_switch));

        UiObject allowButton = device.findObject(new UiSelector().text("Allow only while using the app"));

        if(allowButton.waitForExists(500)) {
            try{
                allowButton.click();
                assertEquals(true, solo.isTextChecked("Attach location"));
            } catch (UiObjectNotFoundException e) {

            }
        }

        // Test turning off switch while permissions are allowed
        if(solo.isTextChecked("Attach Location")) {
            solo.clickOnView(solo.getView(R.id.location_switch));
            assertEquals(false, solo.isTextChecked("Attach location"));
        }
    }
}