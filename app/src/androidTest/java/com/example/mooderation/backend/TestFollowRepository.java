package com.example.mooderation.backend;

import com.example.mooderation.Follower;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Tasks;
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
public class TestFollowRepository {
    private FollowRepository followRepository;
    private ParticipantRepository participantRepository;

    private CollectionReference followersPath;
    private Follower mockFollower;
    private Participant p;

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Tasks.await(auth.signInAnonymously());

        followRepository = new FollowRepository();
        participantRepository = new ParticipantRepository();

        DocumentReference userPath = db.collection("users").document(auth.getUid());
        followersPath = userPath.collection("followers");

        mockFollower = new Follower("follower", "follower_name");
        p = new Participant(auth.getUid(), "user");
        Tasks.await(participantRepository.remove(p).continueWith(
                task -> participantRepository.register(p)));
    }

    @Test
    public void testFollow() {

    }

    @Test
    public void testAdd() throws ExecutionException, InterruptedException {
        assertFalse(Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
        followRepository.add(p, mockFollower);
        assertEquals(mockFollower, Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().toObject(Follower.class))));
    }

    @Test
    public void testRemove() throws ExecutionException, InterruptedException {
        Tasks.await(followersPath.document(mockFollower.getUid()).set(mockFollower));
        assertEquals(mockFollower, Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().toObject(Follower.class))));
        followRepository.remove(p, mockFollower);
        assertFalse(Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
    }
}
