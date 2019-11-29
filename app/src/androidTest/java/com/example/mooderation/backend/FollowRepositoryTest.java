package com.example.mooderation.backend;

import android.provider.Telephony;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FollowRepositoryTest {
    private static FirebaseFirestore firestore;

    // mock participants used for testing
    private static Participant myParticipant = new Participant("my-uid", "me");
    private static Participant otherParticipant = new Participant("other-uid", "other");

    // mock FirebaseUsers to be be injected into the repository
    @Mock
    FirebaseUser myUser;
    @Mock
    FirebaseUser otherUser;

    // mock observers -- forces test to wait for LiveData to update
    @Mock
    Observer<List<FollowRequest>> requestObserver;
    @Mock
    Observer<List<Participant>> followersObserver;
    @Mock
    Observer<Boolean> booleanObserver;

    // repositories for testing
    private FollowRepository myFollowRepository;
    private FollowRepository otherFollowRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        firestore = FirebaseFirestore.getInstance();

        // create documents for users
        FirebaseUtils.createUser(firestore, myParticipant);
        FirebaseUtils.createUser(firestore, otherParticipant);
    }

    @Before
    public void setUp() {
        // prepare mock FirebaseUsers
        MockitoAnnotations.initMocks(this);
        when(myUser.getUid()).thenReturn(myParticipant.getUid());
        when(otherUser.getUid()).thenReturn(otherParticipant.getUid());

        // set up myParticipant follow repository
        myFollowRepository = new FollowRepository(myUser, firestore);

        // set up otherParticipant follow repository
        otherFollowRepository = new FollowRepository(otherUser, firestore);
        otherFollowRepository.getFollowRequests().observeForever(requestObserver);
        otherFollowRepository.getFollowers().observeForever(followersObserver);
    }

    @Test
    public void testFollow() throws InterruptedException, ExecutionException {
        // send a follow request
        Tasks.await(myFollowRepository.follow(otherParticipant));

        // check that the request is in the database
        assertNotNull(otherFollowRepository.getFollowRequests().getValue());
        assertEquals(1, otherFollowRepository.getFollowRequests().getValue().size());

        // check that the request has the correct information
        FollowRequest request = otherFollowRepository.getFollowRequests().getValue().get(0);
        assertEquals(myParticipant.getUid(), request.getUid());
        assertEquals(myParticipant.getUsername(), request.getUsername());
    }

    @Test
    public void testAccept() throws ExecutionException, InterruptedException {
        // send a follow request
        Tasks.await(myFollowRepository.follow(otherParticipant));
        FollowRequest request = otherFollowRepository.getFollowRequests().getValue().get(0);

        // accept the request
        Tasks.await(otherFollowRepository.accept(request));

        // assert that the request has been removed
        assertEquals(0, otherFollowRepository.getFollowRequests().getValue().size());

        // assert the the follower has been added
        assertNotNull(otherFollowRepository.getFollowers().getValue());
        assertEquals(1, otherFollowRepository.getFollowers().getValue().size());

        Participant follower = otherFollowRepository.getFollowers().getValue().get(0);
        assertEquals(myParticipant.getUid(), follower.getUid());
        assertEquals(myParticipant.getUsername(), follower.getUsername());


        LiveData<List<Participant>> followingLiveData = myFollowRepository.getFollowing();
        Thread.sleep(100);
        assertNotNull(followingLiveData.getValue());
        assertEquals(1, followingLiveData.getValue().size());

        Participant following = followingLiveData.getValue().get(0);
        assertEquals(otherParticipant.getUid(), following.getUid());
        assertEquals(otherParticipant.getUsername(), following.getUsername());
    }

    @Test
    public void testDeny() throws ExecutionException, InterruptedException {
        // send a follow request
        Tasks.await(myFollowRepository.follow(otherParticipant));
        FollowRequest request = otherFollowRepository.getFollowRequests().getValue().get(0);

        // deny the request
        Tasks.await(otherFollowRepository.deny(request));

        // assert that the request has been removed
        assertEquals(0, otherFollowRepository.getFollowRequests().getValue().size());
    }

    @Test
    public void testIsRequestSent() throws ExecutionException, InterruptedException {
        LiveData<Boolean> requestSent = myFollowRepository.isRequestSent(otherParticipant);

        assertNotNull(requestSent);
        requestSent.observeForever(booleanObserver);

        // send and accept the follow request request
        Tasks.await(myFollowRepository.follow(otherParticipant));

//        verify(booleanObserver).onChanged(true);
        assertTrue(requestSent.getValue()); // TODO find out why observer is called twice
    }

    @Test
    public void testIsFollowing() throws ExecutionException, InterruptedException {
        LiveData<Boolean> following = myFollowRepository.isFollowing(otherParticipant);

        assertNotNull(following);
        following.observeForever(booleanObserver);

        // send and accept the follow request request
        Tasks.await(myFollowRepository.follow(otherParticipant));
        FollowRequest request = otherFollowRepository.getFollowRequests().getValue().get(0);
        Tasks.await(otherFollowRepository.accept(request));

        // verify observer is notified
        verify(booleanObserver).onChanged(true);
    }
}
