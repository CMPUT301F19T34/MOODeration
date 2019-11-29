package com.example.mooderation.intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

public class AddMoodEventTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("test@email.com", "password"));
    }

    private void clearMoodHistory(FirebaseUser user) throws ExecutionException, InterruptedException {
        // get mood history reference
        QuerySnapshot moodHistoryReference = Tasks.await(
                FirebaseFirestore.getInstance().collection("users")
                        .document(user.getUid()).collection("mood_history").get());

        // delete all mood events
        List<Task<Void>> tasks = new ArrayList<>();
        for (DocumentSnapshot doc : moodHistoryReference) {
            tasks.add(doc.getReference().delete());
        }

        // wait until completion
        Tasks.await(Tasks.whenAll(tasks));
    }

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        clearMoodHistory(FirebaseAuth.getInstance().getCurrentUser());
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testAddMoodEvent() {
        // add new mood event
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));

        // check for text
        assertTrue(solo.waitForText("Happy"));
    }

    @Test
    public void testSpinners() {
        // open mood event fragment
        solo.clickOnView(solo.getView(R.id.add_mood_event_button));

        // set emotional state
        solo.clickOnView(solo.getView(R.id.emotional_state_spinner));
        solo.clickInList(3);

        // set social situation
        solo.clickOnView(solo.getView(R.id.social_situation_spinner));
        solo.clickInList(2);

        // save mood event fragments and open details
        solo.clickOnView(solo.getView(R.id.save_mood_event_button));
        solo.clickInList(0);

        // check result
        assertTrue(solo.waitForText("Mad"));
        assertTrue(solo.waitForText("Alone"));
    }

    @Test
    public void testReason() {
        // open mood event fragment
        solo.clickOnView(solo.getView(R.id.add_mood_event_button));

        // set reason text
        solo.clickOnView(solo.getView((R.id.reason_edit_text)));
        solo.typeText(0,"My reason test");

        // save mood event fragments and open details
        solo.clickOnView(solo.getView(R.id.save_mood_event_button));
        solo.clickInList(0);

        // check result
        assertTrue(solo.waitForText("My reason test"));
    }

    // TODO test location

    @Test
    public void testAddPhoto() {
        // TODO may not be possible with robotium
        //solo.clickOnView(solo.getView(R.id.take_photo_button));

    }
}
