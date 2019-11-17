package com.example.mooderation;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.mooderation.backend.FollowRequestRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.example.mooderation.viewmodel.FollowRequestsViewModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
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
public class FollowRequestsViewModelTest {
    private boolean observed = false;

    private Participant participant;

    private ParticipantRepository participantRepository;
    private FollowRequestRepository followRequestRepository;

    private FollowRequestsViewModel followRequestsViewModel;

    // TODO I don't think this currently does anything
    // forces tasks to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private FollowRequest mockFollowRequest(String uid) {
        return new FollowRequest(uid, "user", Timestamp.now());
    }

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        // sign in as an anonymous user
        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());
        participant = new Participant(FirebaseAuth.getInstance().getUid(), "user");

        // register the participant to the view model
        followRequestsViewModel = new FollowRequestsViewModel();
        //followRequestsViewModel.setParticipant(participant);

        participantRepository = new ParticipantRepository();
        followRequestRepository = new FollowRequestRepository();

        Tasks.await(participantRepository.remove(participant).continueWith(task -> participantRepository.register(participant)));
    }

    @Test
    public void testAcceptFollowRequest() throws ExecutionException, InterruptedException {
        FollowRequest followRequest = mockFollowRequest("uid");
        Tasks.await(followRequestRepository.add(participant, followRequest));

        // check mood event was added
        assertEquals(1, followRequestsViewModel.getFollowRequests().getValue().size());
        Tasks.await(followRequestsViewModel.acceptRequest(followRequest));

        // check mood event was removed
        assertEquals(0, followRequestsViewModel.getFollowRequests().getValue().size());
    }

    @Test
    public void testDenyFollowRequest() throws ExecutionException, InterruptedException {
        FollowRequest followRequest = mockFollowRequest("uid");
        Tasks.await(followRequestRepository.add(participant, followRequest));

        // check mood event was added
        assertEquals(1, followRequestsViewModel.getFollowRequests().getValue().size());
        Tasks.await(followRequestsViewModel.denyRequest(followRequest));

        // check mood event was removed
        assertEquals(0, followRequestsViewModel.getFollowRequests().getValue().size());
    }

    @Test
    public void testFollowRequestOrder() throws ExecutionException, InterruptedException {
        // register mood events in chronological order
        Tasks.await(followRequestRepository.add(participant, mockFollowRequest("uid1")));
        Thread.sleep(10);
        Tasks.await(followRequestRepository.add(participant, mockFollowRequest("uid2")));

        // check for reverse chronological order
        FollowRequest l = followRequestsViewModel.getFollowRequests().getValue().get(0);
        FollowRequest r = followRequestsViewModel.getFollowRequests().getValue().get(1);
        assertTrue(l.getCreateTimestamp().compareTo(r.getCreateTimestamp()) > 0);
    }

    @Test
    public void testFollowRequestObserver() throws ExecutionException, InterruptedException {
        followRequestsViewModel.getFollowRequests().observeForever(new Observer<List<FollowRequest>>() {
            @Override
            public void onChanged(List<FollowRequest> followRequests) {
                observed = true;
            }
        });

        Tasks.await(followRequestRepository.add(participant, mockFollowRequest("uid1")));
        assertTrue(observed);
    }
}
