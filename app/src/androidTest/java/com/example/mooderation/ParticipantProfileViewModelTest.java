package com.example.mooderation;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.mooderation.backend.FollowRequestRepository;
import com.example.mooderation.backend.FollowerRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.example.mooderation.viewmodel.ParticipantProfileViewModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ParticipantProfileViewModelTest {
    private boolean observed = false;

    private Participant user;
    private Participant other;

    private ParticipantRepository participantRepository;
    private FollowerRepository followerRepository;
    private FollowRequestRepository followRequestRepository;

    private ParticipantProfileViewModel participantProfileViewModel;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        // sign in as an anonymous user
        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());
        user = new Participant(FirebaseAuth.getInstance().getUid(), "user");
        other = new Participant("other_id", "other_name");

        // add the user to the view model
        participantProfileViewModel = new ParticipantProfileViewModel();
        participantProfileViewModel.setParticipant(user);
        participantProfileViewModel.setViewingParticipant(other);

        participantRepository = new ParticipantRepository();
        followRequestRepository = new FollowRequestRepository();
        followerRepository = new FollowerRepository();

        Tasks.await(participantRepository.remove(user).continueWith(task -> participantRepository.add(user)));
        Tasks.await(participantRepository.remove(other).continueWith(task -> participantRepository.add(other)));
    }

    @Test
    public void testAcceptRequest() throws ExecutionException, InterruptedException {
        participantProfileViewModel.sendFollowRequest();
        List<FollowRequest> requests = RepoUtil.get(followRequestRepository, other);
        assertEquals(1, requests.size());
        assertEquals(user.getUid(), requests.get(0).getUid());
        assertEquals(user.getUsername(), requests.get(0).getUsername());
    }

    @Test
    public void testRequestSent() throws InterruptedException {
        participantProfileViewModel.sendFollowRequest();
        Thread.sleep(1000);
        assertTrue(participantProfileViewModel.getFollowRequestSent().getValue());
    }

    @Test
    public void testIsFollowing() throws InterruptedException {
        followerRepository.add(other, Follower.fromParticipant(user));
        Thread.sleep(1000);
        assertTrue(participantProfileViewModel.getThisFollowingOther().getValue());
    }

    @Test
    public void testUsername() {
        assertEquals("other_name", participantProfileViewModel.getUsername().getValue());
    }
}
