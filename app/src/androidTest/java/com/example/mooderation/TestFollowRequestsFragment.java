package com.example.mooderation;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import com.example.mooderation.backend.FollowRequestRepository;
import com.example.mooderation.backend.FollowerRepository;
import com.example.mooderation.backend.OwnedRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
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
    private FollowRequestRepository followRequestRepository;
    private FollowerRepository followerRepository;
    private ParticipantRepository participantRepository;

    private Participant p;
    private FollowRequest mockFollowRequest1;
    private FollowRequest mockFollowRequest2;

    private Follower mockFollower1 = new Follower("uid1", "name1");
    private Follower mockFollower2 = new Follower("uid2", "name2");

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        followerRepository = new FollowerRepository();
        followRequestRepository = new FollowRequestRepository();
        participantRepository = new ParticipantRepository();

        mockFollowRequest1 = new FollowRequest("uid1", "name1", Timestamp.now());
        Thread.sleep(1);
        mockFollowRequest2 = new FollowRequest("uid2", "name2", Timestamp.now());

        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());

        p = new Participant(FirebaseAuth.getInstance().getUid(), "user");
        Bundle args = new Bundle();
        args.putString("uid", p.getUid());
        args.putString("username", p.getUsername());
        FragmentScenario.launchInContainer(FollowRequestsFragment.class, args);

        Tasks.await(participantRepository.remove(p).continueWith(task -> participantRepository.add(p)));
        Tasks.await(followRequestRepository.add(p, mockFollowRequest1)
                .continueWithTask(task -> followRequestRepository.add(p, mockFollowRequest2)));
    }

    @Test
    public void testAcceptFollower() throws ExecutionException, InterruptedException {
        onData(anything()).inAdapterView(withId(R.id.follow_request_list)).atPosition(0).perform(click());
        onView(withText("Accept")).perform(click());

        assertTrue(RepoUtil.contains(followerRepository, p, mockFollower2));
        assertFalse(RepoUtil.contains(followRequestRepository, p, mockFollowRequest2));
    }

    @Test
    public void testDenyFollower() throws ExecutionException, InterruptedException {
        onData(anything()).inAdapterView(withId(R.id.follow_request_list)).atPosition(0).perform(click());
        onView(withText("Deny")).perform(click());
        assertFalse(RepoUtil.contains(followerRepository, p, mockFollower2));
        assertFalse(RepoUtil.contains(followRequestRepository, p, mockFollowRequest2));
    }


}
