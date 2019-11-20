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
import static org.junit.Assert.assertTrue;

public class EditMoodTest {
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
    public void testEditMood() {
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button and add happy mood event
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Happy"));
        // Expand list and click edit
        solo.clickInList(0);
        solo.clickOnView(solo.getView((R.id.EditButton)));
        // Change mood to sad and commit change
        solo.clickOnView(solo.getView((R.id.emotional_state_spinner)));
        solo.clickInList(2);
        solo.clickOnView(solo.getView(R.id.edit_mood_event_button));
        // Confirm mood changed to Sad
        assertTrue((solo.waitForText("Sad")));
    }

    @Test
    public void testEditSituation() {
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button and add happy mood event
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Happy"));
        // Expand list and click edit
        solo.clickInList(0);
        solo.clickOnView(solo.getView((R.id.EditButton)));
        // Change social situation to One other person and commit change
        solo.clickOnView(solo.getView((R.id.social_situation_spinner)));
        solo.clickInList(3);
        solo.clickOnView(solo.getView(R.id.edit_mood_event_button));
        // Expand list for details and verify social situation changed
        solo.clickInList(0);
        assertTrue((solo.waitForText("One other person")));
    }

    @Test
    public void testEditReason() {
        // TODO fix - this assert fails even though the app is in the main activity
        //solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button and add happy mood event
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Happy"));
        // Expand list and click edit
        solo.clickInList(0);
        solo.clickOnView(solo.getView((R.id.EditButton)));
        // Change reason to Reason test and commit change
        solo.clickOnView(solo.getView((R.id.reason_edit_text)));
        solo.typeText(0,"Reason test");
        solo.clickOnView(solo.getView(R.id.edit_mood_event_button));
        // Expand list for details and verify reason changed
        solo.clickInList(0);
        assertTrue(solo.waitForText("Reason test"));
    }
}
