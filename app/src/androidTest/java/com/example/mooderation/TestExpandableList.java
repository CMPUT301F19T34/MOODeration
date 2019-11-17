package com.example.mooderation;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.backend.ParticipantRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestExpandableList {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(
            MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());
        ParticipantRepository participantRepository = new ParticipantRepository();
        Participant p = new Participant(FirebaseAuth.getInstance().getUid(), "user");
        Tasks.await(participantRepository.remove(p).continueWith(task -> participantRepository.register(p)));
    }

    @Test
    public void testDropMenu() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Activity activity = rule.getActivity();

        // click the floating action button
        solo.clickOnView(solo.getView((R.id.add_mood_event_button)));
        solo.clickOnView(solo.getView((R.id.save_mood_event_button)));
        assertTrue(solo.waitForText("Happy"));
        solo.clickInList(0);

        solo.clickOnView(solo.getView((R.id.EditButton)));
        solo.clickOnView(solo.getView((R.id.reason_edit_text)));
        //solo.enterText(R.id.reason_edit_text, "Reason");
        solo.clickOnView(solo.getView(R.id.edit_mood_event_button));
        //assertTrue(solo.waitForFragmentById(R.id.editMoodEventFragment));

    }
}
