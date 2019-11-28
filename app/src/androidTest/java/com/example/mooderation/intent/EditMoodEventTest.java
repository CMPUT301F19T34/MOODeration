package com.example.mooderation.intent;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EditMoodEventTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("test@email.com", "password"));
    }

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testEditMood() {
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
        solo.clickOnView(solo.getView(R.id.save_mood_event_button));

        // Confirm mood changed to Sad
        assertTrue((solo.waitForText("Sad")));
    }

    @Test
    public void testEditSituation() {
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
        solo.clickOnView(solo.getView(R.id.save_mood_event_button));

        // Expand list for details and verify social situation changed
        solo.clickInList(0);
        assertTrue((solo.waitForText("One other person")));
    }

    @Test
    public void testEditReason() {
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
        solo.clickOnView(solo.getView(R.id.save_mood_event_button));

        // Expand list for details and verify reason changed
        solo.clickInList(0);
        assertTrue(solo.waitForText("Reason test"));
    }

    @Test
    public void testInitialValues() {
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
        TextView dateText = (TextView) solo.getView(R.id.date_picker_button);
        TextView timeText = (TextView) solo.getView(R.id.time_picker_button);

        assertEquals(emotionalStateSpinner.getSelectedItem(), EmotionalState.SAD);
        assertEquals(socialSituationSpinner.getSelectedItem(), SocialSituation.ALONE);
        assertEquals(reasonText.getText().toString(), "Reason");
        assertEquals(dateText.getText().toString(), dateString);
        assertEquals(timeText.getText().toString(), timeString);
    }
}
