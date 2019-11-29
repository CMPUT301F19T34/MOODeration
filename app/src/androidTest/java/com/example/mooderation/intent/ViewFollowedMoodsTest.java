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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

public class ViewFollowedMoodsTest {
    private Solo solo;

    private FollowedMoodEventRepository followedMoodEventRepository = new FollowedMoodEventRepository();
    private ParticipantRepository participantRepository = new ParticipantRepository();
    private FollowRepository followRepository;
    private Random random = new Random();

    private List<Participant> participants = new ArrayList<>();
    private List<MoodEvent> moodEvents = new ArrayList<>();

    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());
    }

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        followRepository = new FollowRepository();
        for (int i = 1; i < 5; ++i) {
            Participant participant = new Participant("uid " + Integer.toString(i), "name " + Integer.toString(i));
            participants.add(participant);
            Tasks.await(participantRepository.remove(participant));
            Tasks.await(participantRepository.register(participant));
            Tasks.await(followRepository.follow(participant));
            Tasks.await(followRepository.acceptAs(participant.getUid(), new FollowRequest(FirebaseAuth.getInstance().getUid(), "test-username", Timestamp.now())));

            Calendar calendar = Calendar.getInstance();
            calendar.set(2019, 1, 1, 1, 1, i);
            MoodEvent moodEvent = new MoodEvent(
                    calendar.getTime(),
                    EmotionalState.HAPPY,
                    SocialSituation.NONE,
                    "Reason text",
                    null
            );
            moodEvents.add(moodEvent);
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
        participants.clear();
        moodEvents.clear();
        solo.finishOpenedActivities();
    }

    // TODO this should be refactored somewhere
    /**
     * Navigates to the followed moods fragment
     */
    private void navigateToFollowedMoodsFragment() {
        // open nav drawer
        solo.clickOnImageButton(0);

        // get maps item
        String label = rule.getActivity().getString(R.string.followed_moods_label);

        // navigate to follow request
        TestCase.assertTrue(solo.waitForText(label, 1, 2000));
        solo.clickOnText(label);
    }

    @Test
    public void testDisplaysOrdered() throws InterruptedException {
        navigateToFollowedMoodsFragment();
        Thread.sleep(1000);

        int[] location = new int[2];

        for (int i = 1; i < participants.size(); ++i) {
            solo.getText(participants.get(i-1).getUsername()).getLocationOnScreen(location);
            int y1 = location[1];
            solo.getText(participants.get(i).getUsername()).getLocationOnScreen(location);
            int y2 = location[1];

            assertTrue(y1 > y2);
        }
    }
}
