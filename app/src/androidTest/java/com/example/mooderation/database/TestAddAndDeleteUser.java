package com.example.mooderation.database;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Follower;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.Database;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class TestAddAndDeleteUser {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private DocumentReference userPath;
    private DocumentReference followerPath;

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Tasks.await(auth.signInAnonymously());

        userPath = db.collection("users").document(auth.getUid());
        followerPath = db.collection("users").document("follower");
    }

    @Test
    public void testAddUser() throws ExecutionException, InterruptedException {
        Tasks.await(userPath.delete());
        assertFalse(Tasks.await(userPath.get().continueWith(task -> task.getResult().exists())));
        new Database().addUser(new Participant(auth.getUid(), "user"));
        assertTrue(Tasks.await(userPath.get().continueWith(task -> task.getResult().exists())));
        assertEquals(new Participant(auth.getUid(), "user"), Tasks.await(
                userPath.get().continueWith(task -> task.getResult().toObject(Participant.class))
        ));
    }

    @Test
    public void testDeleteUser() throws ExecutionException, InterruptedException {
        Participant p = new Participant(auth.getUid(), "test_username");
        Tasks.await(userPath.set(p));
        Tasks.await(userPath.collection("followers")
                .document("test_follower")
                .set(new Follower("test_follower", "test_follower_username")));
        Tasks.await(userPath.collection("follow_requests")
                .document("test_follower")
                .set(new FollowRequest("test_follower_username", "test_username", Timestamp.now())));
        Tasks.await(followerPath.collection("follow_requests")
                .document(auth.getUid())
                .set(new FollowRequest(auth.getUid(), "user", Timestamp.now())));
        Tasks.await(followerPath.collection("followers")
                .document(auth.getUid())
                .set(new Follower(auth.getUid(), "user")));

        Tasks.await(new Database().deleteUser(p));

        assertFalse(Tasks.await(
                userPath
                        .collection("followers")
                        .document("test_follower")
                        .get()
                        .continueWith(task -> task.getResult().exists())
        ));
        assertFalse(Tasks.await(
                userPath
                        .collection("follow_requests")
                        .document("test_follower")
                        .get()
                        .continueWith(task -> task.getResult().exists())
        ));
        assertFalse(Tasks.await(userPath.get().continueWith(task -> task.getResult().exists())));
        assertFalse(Tasks.await(
                followerPath
                        .collection("followers")
                        .document(auth.getUid())
                        .get()
                        .continueWith(task -> task.getResult().exists())
        ));
        assertFalse(Tasks.await(
                followerPath
                        .collection("follow_requests")
                        .document(auth.getUid())
                        .get()
                        .continueWith(task -> task.getResult().exists())
        ));
    }
}