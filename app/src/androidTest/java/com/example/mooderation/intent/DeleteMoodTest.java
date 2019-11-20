package com.example.mooderation.intent;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.example.mooderation.SplashActivity;
import com.example.mooderation.auth.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.example.mooderation.intent.AuthUtils.login;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteMoodTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SplashActivity> rule = new ActivityTestRule<>(
            SplashActivity.class, true, true);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(LoginActivity.class, 1000);
        login(solo);
        solo.waitForActivity(HomeActivity.class, 1000);
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testDeleteMood() {
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button and add two happy mood events
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Happy"));
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Happy"));

        // Expand list and click delete
        solo.clickInList(0);
        solo.clickOnView(solo.getView((R.id.DeleteButton)));
        solo.clickOnText("Confirm");
        solo.waitForDialogToClose();
        // Expand list and click delete again
        solo.clickInList(0);
        solo.clickOnView(solo.getView((R.id.DeleteButton)));
        solo.clickOnText("Confirm");
        solo.waitForDialogToClose();
        // Confirm both deletions happened
        assertFalse(solo.waitForText("Happy", 1, 5));
    }
}
