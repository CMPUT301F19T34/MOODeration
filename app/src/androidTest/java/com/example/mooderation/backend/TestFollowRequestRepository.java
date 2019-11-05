package com.example.mooderation.backend;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Follower;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class TestFollowRequestRepository {
    private FollowRequestRepository followRequestRepository;
    private ParticipantRepository participantRepository;

    private CollectionReference followRequestsPath;
    private FollowRequest mockFollowRequest;
    private Participant p;

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        followRequestRepository = new FollowRequestRepository();
        participantRepository = new ParticipantRepository();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Tasks.await(auth.signInAnonymously());

        DocumentReference userPath = db.collection("users").document(auth.getUid());
        followRequestsPath = userPath.collection("follow_requests");

        mockFollowRequest = new FollowRequest("follower", "follower_name", Timestamp.now());
        p = new Participant(auth.getUid(), "user");
        Tasks.await(participantRepository.remove(p).continueWith(task -> participantRepository.add(p)));
    }

    @Test
    public void testAdd() throws ExecutionException, InterruptedException {
        assertFalse(Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
        followRequestRepository.add(p, mockFollowRequest);
        assertEquals(mockFollowRequest, Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                .continueWith(task -> task.getResult().toObject(FollowRequest.class))));
    }

    @Test
    public void testRemove() throws ExecutionException, InterruptedException {
        Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).set(mockFollowRequest));
        assertEquals(mockFollowRequest, Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                .continueWith(task -> task.getResult().toObject(FollowRequest.class))));
        followRequestRepository.remove(p, mockFollowRequest);
        assertFalse(Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
    }
}
