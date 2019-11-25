package com.example.mooderation.intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.HomeActivity;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodLatLng;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.backend.MoodEventRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
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
 * Intent tests for the mood history map view
 */
public class MoodHistoryMapTest {
    private Solo solo;

    private MoodEventRepository moodEventRepository = new MoodEventRepository();
    private Random random = new Random();

    private List<MoodEvent> moodEvents = new ArrayList<>();

    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("test@email.com", "password"));
    }

    @Before
    public void setUp() throws InterruptedException {
        moodEvents.clear();
        for (EmotionalState emotionalState: EmotionalState.values()) {
            moodEvents.add(new MoodEvent(
                    new Date(),
                    emotionalState,
                    SocialSituation.NONE,
                    "Reason text",
                    new MoodLatLng(53.631611 + random.nextDouble()*0.1, -113.323975 + random.nextDouble()*0.1)
            ));
            Thread.sleep(1);
        }

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        for (MoodEvent moodEvent : moodEvents) {
            moodEventRepository.remove(moodEvent);
        }
        solo.finishOpenedActivities();
    }

    @Test
    public void testEmpty() throws InterruptedException {
        navigateToMoodHistoryMapFragment();
        Thread.sleep(1000);
    }

    @Test
    public void testOneLocation() throws InterruptedException {
        moodEventRepository.add(moodEvents.get(0));
        navigateToMoodHistoryMapFragment();
        Thread.sleep(1000);
    }

    @Test
    public void testManyLocations() throws InterruptedException {
        for (MoodEvent e : moodEvents) {
            moodEventRepository.add(e);
        }
        navigateToMoodHistoryMapFragment();
        Thread.sleep(1000);
    }

    // TODO this should be refactored somewhere
    /**
     * Navigates to the follow request fragment
     */
    private void navigateToMoodHistoryMapFragment() {
        // open nav drawer
        solo.clickOnImageButton(0);

        // get maps item
        String label = rule.getActivity().getString(R.string.mood_history_map_label);

        // navigate to follow request
        assertTrue(solo.waitForText(label, 1, 2000));
        solo.clickOnText(label);
    }
}
