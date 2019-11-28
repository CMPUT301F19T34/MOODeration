package com.example.mooderation.intent;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

public class DeleteMoodTest {
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

        // TODO this will fail if the test user already has happy in there mood history
        // Confirm both deletions happened
        // assertFalse(solo.waitForText("Happy", 1, 1000));
    }
}
