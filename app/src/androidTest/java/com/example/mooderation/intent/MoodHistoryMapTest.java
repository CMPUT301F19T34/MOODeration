package com.example.mooderation.intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.HomeActivity;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodLatLng;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.SplashActivity;
import com.example.mooderation.auth.ui.LoginActivity;
import com.example.mooderation.backend.MoodEventRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.example.mooderation.intent.AuthUtils.login;
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
    public ActivityTestRule<SplashActivity> rule = new ActivityTestRule<>(
            SplashActivity.class, true, true);

    @Before
    public void setUp() throws InterruptedException {
        FirebaseAuth.getInstance().signOut();

        moodEvents.clear();
        for (EmotionalState s: EmotionalState.values()) {
            moodEvents.add(new MoodEvent(
                    new Date(),
                    s,
                    SocialSituation.NONE,
                    "Reason text",
                    new MoodLatLng(53.631611 + random.nextDouble()*0.1, -113.323975 + random.nextDouble()*0.1)
            ));
            Thread.sleep(1);
        }

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(LoginActivity.class, 1000);
        login(solo);
        solo.waitForActivity(HomeActivity.class, 1000);
    }

    @After
    public void tearDown() {
        for (MoodEvent e : moodEvents) {
            moodEventRepository.remove(e);
        }
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
