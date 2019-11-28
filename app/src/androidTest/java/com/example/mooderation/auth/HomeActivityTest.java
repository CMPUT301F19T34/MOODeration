package com.example.mooderation.auth;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HomeActivityTest {
    private Solo solo;
    private UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(
            HomeActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    @Ignore("Takes too long")
    public void testToggleLocation() {
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));

        // if location permission is denied
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if deny and don't show again was selected
            if(!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                UiObject okButton = device.findObject(new UiSelector().text("OK"));

                if(okButton.waitForExists(500)) {
                    try {
                        okButton.click();
                        assertEquals(false, solo.isTextChecked("Attach location"));
                    } catch (UiObjectNotFoundException e) {

                    }
                }
            }
            // Test denying location permission
            solo.clickOnView(solo.getView(R.id.location_switch));
            // Initialize UiDevice instance
            UiObject denyButton = device.findObject(new UiSelector().textStartsWith("Deny"));

            if(denyButton.waitForExists(500)) {
                try {
                    denyButton.click();
                    assertEquals(false, solo.isTextChecked("Attach location"));
                } catch (UiObjectNotFoundException e) {
                    UiObject DENYButton = device.findObject(new UiSelector().textStartsWith("DENY"));
                    try {
                        DENYButton.click();
                        assertEquals(false, solo.isTextChecked("Attach location"));
                    } catch (UiObjectNotFoundException e2) {

                    }
                }
            }

            // Next, test allowing location permission from off-state
            solo.clickOnView(solo.getView(R.id.location_switch));

            UiObject allowButton = device.findObject(new UiSelector().textStartsWith("Allow"));

            if(allowButton.waitForExists(500)) {
                try{
                    allowButton.click();
                    assertEquals(true, solo.isTextChecked("Attach location"));
                } catch (UiObjectNotFoundException e) {

                }
            }
        // if permission is granted
        } else {
            solo.clickOnView(solo.getView(R.id.location_switch));
            assertEquals(true, solo.isTextChecked("Attach location"));

            solo.clickOnView(solo.getView(R.id.location_switch));
            assertEquals(false, solo.isTextChecked("Attach location"));
        }

        // Test turning off switch while permissions are allowed
        if(solo.isTextChecked("Attach Location")) {
            solo.clickOnView(solo.getView(R.id.location_switch));
            assertEquals(false, solo.isTextChecked("Attach location"));
        }
    }
}