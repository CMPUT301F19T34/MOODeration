package com.example.mooderation.database;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Follower;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.Database;
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
public class TestAddAndDelete {
    private Database database;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private DocumentReference userPath;
    private CollectionReference followersPath;
    private CollectionReference followRequestsPath;
    private FollowRequest mockFollowRequest;
    private Follower mockFollower;

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Tasks.await(auth.signInAnonymously());

        database = new Database();

        userPath = db.collection("users").document(auth.getUid());
        followersPath = userPath.collection("followers");
        followRequestsPath = userPath.collection("follow_requests");

        mockFollowRequest = new FollowRequest("follower", "follower_name", Timestamp.now());
        mockFollower = new Follower("follower", "follower_name");

        Participant p = new Participant(auth.getUid(), "user");
        Tasks.await(database.deleteUser(p).continueWith(task -> database.addUser(p)));
    }

    @Test
    public void testAddFollowRequest() throws ExecutionException, InterruptedException {
        assertFalse(Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
        database.addFollowRequest(mockFollowRequest);
        assertEquals(mockFollowRequest, Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                        .continueWith(task -> task.getResult().toObject(FollowRequest.class))));
    }

    @Test
    public void testDeleteFollowRequest() throws ExecutionException, InterruptedException {
        Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).set(mockFollowRequest));
        assertEquals(mockFollowRequest, Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                .continueWith(task -> task.getResult().toObject(FollowRequest.class))));
        database.deleteFollowRequest(mockFollowRequest);
        assertFalse(Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
    }
    
    @Test
    public void testAddFollower() throws ExecutionException, InterruptedException {
        assertFalse(Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
        database.addFollower(mockFollower);
        assertEquals(mockFollower, Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().toObject(Follower.class))));
    }

    @Test
    public void testDeleteFollower() throws ExecutionException, InterruptedException {
        Tasks.await(followersPath.document(mockFollower.getUid()).set(mockFollower));
        assertEquals(mockFollower, Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().toObject(Follower.class))));
        database.deleteFollower(mockFollower);
        assertFalse(Tasks.await(followersPath.document(mockFollower.getUid()).get()
                .continueWith(task -> task.getResult().exists())));
    }
}
