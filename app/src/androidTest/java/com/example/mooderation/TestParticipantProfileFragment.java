package com.example.mooderation;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import com.example.mooderation.backend.FollowRequestRepository;
import com.example.mooderation.backend.FollowerRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TestParticipantProfileFragment {
    private FollowerRepository followerRepository;
    private FollowRequestRepository followRequestRepository;

    private Participant user;
    private Participant other;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        followerRepository = new FollowerRepository();
        followRequestRepository = new FollowRequestRepository();

        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());

        user = new Participant(FirebaseAuth.getInstance().getUid(), "user");
        other = new Participant("other_id", "other_name");

        Bundle args = new Bundle();
        args.putString("user_uid", user.getUid());
        args.putString("user_username", user.getUsername());
        args.putString("other_uid", other.getUid());
        args.putString("other_username", other.getUsername());
        FragmentScenario.launchInContainer(ParticipantProfileFragment.class, args);

        ParticipantRepository participantRepository = new ParticipantRepository();
        Tasks.await(participantRepository.remove(user).continueWith(task -> participantRepository.add(user)));
        Tasks.await(participantRepository.remove(other).continueWith(task -> participantRepository.add(other)));
    }

    @Test
    public void testSendFollowRequest() throws ExecutionException, InterruptedException {
        onView(withId(R.id.follow_button)).perform(click());
        // onData(anything()).inAdapterView(withId(R.id.follow_request_list)).atPosition(0).perform(click());
        onView(withText("Send request")).perform(click());
        List<FollowRequest> requests = RepoUtil.get(followRequestRepository, other);
        assertEquals(1, requests.size());
        assertEquals(user.getUid(), requests.get(0).getUid());
        assertEquals(user.getUsername(), requests.get(0).getUsername());
    }

    @Test
    public void testDisableButton() throws ExecutionException, InterruptedException {
        onView(withId(R.id.follow_button)).check(matches(isEnabled()));
        Tasks.await(followerRepository.add(other, Follower.fromParticipant(user)));
        Thread.sleep(5000);
        onView(withId(R.id.follow_button)).check(matches(not(isEnabled())));
    }

    @Test
    public void testUsernameShown() {
        onView(withId(R.id.username)).check(matches(withText(other.getUsername())));
    }
}
