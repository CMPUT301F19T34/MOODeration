package com.example.mooderation.intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.FollowRequest;
import com.example.mooderation.HomeActivity;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodLatLng;
import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.backend.FollowRepository;
import com.example.mooderation.backend.FollowedMoodEventRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;

/**
 * Intent tests for the followed mood history map view
 */
public class FollowedMoodHistoryMapTest {
    private Solo solo;

    private FollowedMoodEventRepository followedMoodEventRepository = new FollowedMoodEventRepository();
    private ParticipantRepository participantRepository = new ParticipantRepository();
    private FollowRepository followRepository;
    private Random random = new Random();

    private List<Participant> participants = new ArrayList<>();

    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("test@email.com", "password"));
    }

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        followRepository = new FollowRepository();
        for (EmotionalState emotionalState: EmotionalState.values()) {
            Participant participant = new Participant("uid " + emotionalState.getStringResource(), "name " + emotionalState.getStringResource());
            participants.add(participant);
            Tasks.await(participantRepository.remove(participant));
            Tasks.await(participantRepository.register(participant));
            Tasks.await(followRepository.follow(participant));
            Tasks.await(followRepository.acceptAs(participant.getUid(), new FollowRequest(FirebaseAuth.getInstance().getUid(), "test-username", Timestamp.now())));

            MoodEvent moodEvent = new MoodEvent(
                    new Date(),
                    emotionalState,
                    SocialSituation.NONE,
                    "Reason text",
                    new MoodLatLng(53.631611 + random.nextDouble()*0.1, -113.323975 + random.nextDouble()*0.1)
            );
            Tasks.await(
                FirebaseFirestore.getInstance().collection("users")
                        .document(participant.getUid())
                        .collection("mood_history")
                        .document(moodEvent.getId())
                        .set(moodEvent)
            );
            Thread.sleep(10);
        }

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        for (Participant participant : participants) {
            participantRepository.remove(participant);
        }
        solo.finishOpenedActivities();
    }

    @Test
    public void testManyLocations() throws InterruptedException {
        navigateToFollowedMoodHistoryMapFragment();
        Thread.sleep(1000);
    }

    // TODO this should be refactored somewhere
    /**
     * Navigates to the follow request fragment
     */
    private void navigateToFollowedMoodHistoryMapFragment() {
        // open nav drawer
        solo.clickOnImageButton(0);

        // get maps item
        String label = rule.getActivity().getString(R.string.followed_mood_history_map_label);

        // navigate to follow request
        assertTrue(solo.waitForText(label, 1, 2000));
        solo.clickOnText(label);
    }
}
