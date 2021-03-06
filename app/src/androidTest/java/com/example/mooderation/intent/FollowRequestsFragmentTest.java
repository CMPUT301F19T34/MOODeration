package com.example.mooderation.intent;

import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.backend.FollowRepository;
import com.google.android.gms.tasks.Tasks;
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
import static org.mockito.Mockito.when;

public class FollowRequestsFragmentTest {
    private Solo solo;

    @Mock
    FirebaseUser mockUser;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Participant followMe = new Participant("follow-uid", "follow-me");
    private FollowRepository followRepository;

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
        when(mockUser.getUid()).thenReturn(followMe.getUid());
        followRepository = new FollowRepository(mockUser, firestore);

        // add mock user to follow
        Tasks.await(firestore.collection("users").document(followMe.getUid()).set(followMe));
        // TODO clean up user?

        solo = new Solo(getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testAcceptFollower() throws ExecutionException, InterruptedException {
        followCurrentUser();
        navigateToFollowRequestFragment();

        solo.clickInList(0);
        solo.clickOnText("Accept");

        // TODO check repository here?
    }

    @Test
    public void testDenyFollower() throws ExecutionException, InterruptedException {
        followCurrentUser();
        navigateToFollowRequestFragment();

        solo.clickInList(0);
        solo.clickOnText("Deny");

        // TODO do check here
    }

    /**
     * Navigates to the follow request fragment
     */
    private void navigateToFollowRequestFragment() {
        // open nav drawer
        solo.clickOnImageButton(0);

        // get follow request item
        String followRequestLabel = rule.getActivity().getString(R.string.follow_request_label);

        // navigate to follow request
        assertTrue(solo.waitForText(followRequestLabel, 1, 2000));
        solo.clickOnText(followRequestLabel);
    }

    /**
     * Sends a follow request to the current user
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void followCurrentUser() throws ExecutionException, InterruptedException {
        // follow the current user from mock account
        Tasks.await(followRepository.follow(
                new Participant(FirebaseAuth.getInstance().getUid(), "username")));
    }
}
