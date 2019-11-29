package com.example.mooderation.intent;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.HomeActivity;
import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.backend.FollowRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Test sending follow requests
 */
public class ParticipantProfileTest {
    private Solo solo;

    private ParticipantRepository participantRepository = new ParticipantRepository();
    private FollowRepository followRepository;
    private Participant participantToSearch = new Participant("participant_to_search_uid", "participant_to_search");
    private Participant thisParticipant;

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
        followRepository = new FollowRepository();
        thisParticipant = new Participant(FirebaseAuth.getInstance().getUid(), "test-username");

        Tasks.await(participantRepository.remove(participantToSearch).continueWithTask(task ->
                participantRepository.register(participantToSearch)
        ));
        Tasks.await(FirebaseFirestore.getInstance()
            .collection("users")
            .document(participantToSearch.getUid())
            .collection("follow_requests")
            .document(thisParticipant.getUid())
            .delete());
        Tasks.await(FirebaseFirestore.getInstance()
            .collection("users")
            .document(participantToSearch.getUid())
            .collection("followers")
            .document(thisParticipant.getUid())
            .delete());

        solo = new Solo(getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testSendRequest() throws ExecutionException, InterruptedException {
        navigateToParticipantProfileFragment();

        assertTrue(solo.getView(R.id.follow_button).isEnabled());
        assertNotEquals(solo.getView(R.id.request_sent).getVisibility(), View.VISIBLE);
        solo.clickOnButton(0);
        solo.clickOnButton(2);
        assertTrue(solo.getView(R.id.follow_button).isEnabled());
        assertEquals(solo.getView(R.id.request_sent).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testDoNotSendRequest() throws InterruptedException {
        navigateToParticipantProfileFragment();

        assertNotEquals(solo.getView(R.id.request_sent).getVisibility(), View.VISIBLE);
        solo.clickOnButton(0);
        solo.clickOnButton(1);
        assertTrue(solo.getView(R.id.follow_button).isEnabled());
        assertNotEquals(solo.getView(R.id.request_sent).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testAlreadyFollowing() throws ExecutionException, InterruptedException {
        navigateToParticipantProfileFragment();
        assertTrue(solo.getView(R.id.follow_button).isEnabled());
        Tasks.await(followRepository.follow(participantToSearch));
        Tasks.await(followRepository.acceptAs(participantToSearch.getUid(), new FollowRequest(thisParticipant.getUid(), thisParticipant.getUsername(), Timestamp.now())));
        assertFalse(solo.getView(R.id.follow_button).isEnabled());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private void navigateToParticipantProfileFragment() throws InterruptedException {
        // open nav drawer
        solo.clickOnImageButton(0);

        // get search item
        String label = rule.getActivity().getString(R.string.find_participant_label);

        // navigate to fragment
        assertTrue(solo.waitForText(label, 1, 2000));
        solo.clickOnText(label);
        // search for participant and navigate to their profile
        solo.clickOnEditText(0);
        solo.typeText(0,"participant_to_search");
        solo.clickInList(0);
        Thread.sleep(1000);
        assertEquals(((TextView) solo.getView(R.id.username)).getText(), participantToSearch.getUsername());
    }
}
