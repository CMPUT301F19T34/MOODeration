package com.example.mooderation;

import android.app.Activity;
import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.backend.ParticipantRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MainActivityTest {
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
        Tasks.await(participantRepository.remove(p).continueWith(task -> participantRepository.add(p)));
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
}