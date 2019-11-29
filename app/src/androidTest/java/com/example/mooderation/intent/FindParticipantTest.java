package com.example.mooderation.intent;

import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Tests for searching for a participant
 */
public class FindParticipantTest {
    private Solo solo;

    @Mock
    FirebaseUser mockUser;

    private ParticipantRepository participantRepository = new ParticipantRepository();
    private Participant participantToSearch = new Participant("participant_to_search_uid", "participant_to_search");


    @Rule
    public ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("test@email.com", "password"));
    }

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);

        Tasks.await(participantRepository.remove(participantToSearch).continueWithTask(task ->
            participantRepository.register(participantToSearch)
        ));

        solo = new Solo(getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testSearchParticipants() throws ExecutionException, InterruptedException {
        navigateToSearchFragment();

        solo.clickOnEditText(0);
        solo.typeText(0,"participant_to_search");
        solo.clickInList(0);
        Thread.sleep(1000);
        assertEquals(((TextView) solo.getView(R.id.username)).getText(), participantToSearch.getUsername());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private void navigateToSearchFragment() {
        // open nav drawer
        solo.clickOnImageButton(0);

        // get search item
        String label = rule.getActivity().getString(R.string.find_participant_label);

        // navigate to fragment
        assertTrue(solo.waitForText(label, 1, 2000));
        solo.clickOnText(label);
    }
}
