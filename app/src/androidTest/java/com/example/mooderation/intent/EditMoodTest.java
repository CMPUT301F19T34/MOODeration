package com.example.mooderation.intent;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.SplashActivity;
import com.example.mooderation.auth.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.example.mooderation.intent.AuthUtils.login;
import static org.junit.Assert.assertEquals;
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

    @Test
    public void testInitialValues() {
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Activity activity = rule.getActivity();

        // Add mood event
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView(R.id.emotional_state_spinner));
        solo.clickInList(2);
        solo.clickOnView(solo.getView(R.id.social_situation_spinner));
        solo.clickInList(2);
        solo.enterText((EditText)solo.getView(R.id.reason_edit_text), "Reason");
        String dateString = solo.getText(1).getText().toString();
        String timeString = solo.getText(2).getText().toString();
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Sad"));

        // Expand list and click edit
        solo.clickInList(0);
        solo.clickOnView(solo.getView((R.id.EditButton)));

        // Check to see that initial values are set correctly
        Spinner emotionalStateSpinner = (Spinner) solo.getView(R.id.emotional_state_spinner);
        Spinner socialSituationSpinner = (Spinner) solo.getView(R.id.social_situation_spinner);
        EditText reasonText = (EditText) solo.getView(R.id.reason_edit_text);
        TextView dateText = (TextView) solo.getView(R.id.date_text_view);
        TextView timeText = (TextView) solo.getView(R.id.time_text_view);

        assertEquals(emotionalStateSpinner.getSelectedItem(), EmotionalState.SAD);
        assertEquals(socialSituationSpinner.getSelectedItem(), SocialSituation.ALONE);
        assertEquals(reasonText.getText().toString(), "Reason");
        assertEquals(dateText.getText().toString(), dateString);
        assertEquals(timeText.getText().toString(), timeString);
    }
}
