package com.example.mooderation;

import androidx.fragment.app.testing.FragmentScenario;

import com.example.mooderation.backend.Database;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutionException;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class TestFollowRequestsFragment {
    private Database database;

    private FollowRequest mockFollowRequest1;
    private FollowRequest mockFollowRequest2;

    private Follower mockFollower1 = new Follower("uid1", "name1");
    private Follower mockFollower2 = new Follower("uid2", "name2");

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        mockFollowRequest1 = new FollowRequest("uid1", "name1", Timestamp.now());
        Thread.sleep(1);
        mockFollowRequest2 = new FollowRequest("uid2", "name2", Timestamp.now());

        database = new Database();

        FragmentScenario.launchInContainer(FollowRequestsFragment.class);
        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());
        Participant p = new Participant(FirebaseAuth.getInstance().getUid(), "user");
        Tasks.await(database.deleteUser(p).continueWith(task -> database.addUser(p)));
        Tasks.await(database.addFollowRequest(mockFollowRequest1)
                .continueWithTask(task -> database.addFollowRequest(mockFollowRequest2))
                .continueWith(task -> database.deleteFollower(mockFollower1))
                .continueWithTask(task -> database.deleteFollower(mockFollower2)));
    }

    @Test
    public void testAcceptFollower() throws ExecutionException, InterruptedException {
        onData(anything()).inAdapterView(withId(R.id.follow_request_list)).atPosition(0).perform(click());
        onView(withText("Accept")).perform(click());
        assertTrue(Tasks.await(database.getFollowers()).contains(mockFollower2));
        assertFalse(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest2));
    }

    @Test
    public void testDenyFollower() throws ExecutionException, InterruptedException {
        onData(anything()).inAdapterView(withId(R.id.follow_request_list)).atPosition(0).perform(click());
        onView(withText("Deny")).perform(click());
        assertFalse(Tasks.await(database.getFollowers()).contains(mockFollower2));
        assertFalse(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest2));
    }
}
