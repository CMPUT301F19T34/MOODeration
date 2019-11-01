package com.example.mooderation;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.backend.Database;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(JUnit4.class)
public class TestFollowRequests {
    private Solo solo;

    private Database database;

    private FollowRequest mockFollowRequest1 = new FollowRequest("uid1", "name1", Timestamp.now());
    private FollowRequest mockFollowRequest2 = new FollowRequest("uid1", "name1", Timestamp.now());

    private Follower mockFollower1 = new Follower("uid1", "name1");
    private Follower mockFollower2 = new Follower("uid1", "name1");

    @Rule
    public ActivityTestRule<FollowRequestsActivity> rule = new ActivityTestRule<>(FollowRequestsActivity.class, true, true);

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        database = new Database();

        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("user@example.com", "password"));
        Tasks.await(database.addFollowRequest(mockFollowRequest1)
                .continueWithTask(task -> database.addFollowRequest(mockFollowRequest2))
                .continueWith(task -> database.deleteFollower(mockFollower1))
                .continueWithTask(task -> database.deleteFollower(mockFollower2)));
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testAcceptFollower() throws ExecutionException, InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", FollowRequestsActivity.class);

        solo.waitForText(mockFollowRequest1.getUsername(), 1, 2000);
        solo.clickOnText(mockFollowRequest1.getUsername());
        solo.clickOnButton("Accept");
        Thread.sleep(500);
        assertTrue(Tasks.await(database.getFollowers()).contains(mockFollower1));
        assertFalse(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest1));
    }

    @Test
    public void testDenyFollower() throws ExecutionException, InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", FollowRequestsActivity.class);

        solo.waitForText(mockFollowRequest1.getUsername(), 1, 2000);
        solo.clickOnText(mockFollowRequest1.getUsername());
        solo.clickOnButton("Deny");
        Thread.sleep(500);
        assertFalse(Tasks.await(database.getFollowers()).contains(mockFollower1));
        assertFalse(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest1));
    }
}
