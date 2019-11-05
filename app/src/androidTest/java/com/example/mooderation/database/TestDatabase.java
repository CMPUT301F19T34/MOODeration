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
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class TestDatabase {
    private Database database;
    private FirebaseAuth auth;

    private DocumentReference userPath;
    private DocumentReference followerPath;

    private FollowRequest mockFollowRequest;
    private Follower mockFollower;

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Tasks.await(auth.signInAnonymously());

        userPath = db.collection("users").document(auth.getUid());
        followerPath = db.collection("users").document("follower");

        mockFollowRequest = new FollowRequest("follower", "follower_name", Timestamp.now());
        mockFollower = new Follower("follower", "follower_name");

        database = new Database();

        Participant p = new Participant(auth.getUid(), "user");
        Tasks.await(database.deleteUser(p).continueWith(task -> database.addUser(p)));
    }

    @Test
    public void testIsAuthenticated() {
        assertTrue(database.authenticated());
    }

    @Test
    public void testIsAuthenticatedAfterSignout() {
        auth.signOut();
        assertFalse(database.authenticated());
    }

    @Test
    public void testGetFollowRequests() throws ExecutionException, InterruptedException {
        assertFalse(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest));
        database.addFollowRequest(mockFollowRequest);
        assertTrue(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest));
    }
    
    @Test
    public void testGetFollowers() throws ExecutionException, InterruptedException {
        assertFalse(Tasks.await(database.getFollowers()).contains(mockFollower));
        database.addFollower(mockFollower);
        assertTrue(Tasks.await(database.getFollowers()).contains(mockFollower));
    }

    @Test
    public void testIsFollowing() throws ExecutionException, InterruptedException {
        Participant other = new Participant(mockFollower.getUid(), mockFollower.getUsername());
        Tasks.await(database.deleteUser(other).continueWith(task -> database.addUser(other)));
        assertFalse(Tasks.await(database.isFollowing(mockFollower)));
        database.addFollower(mockFollower);
        assertTrue(Tasks.await(database.isFollowing(mockFollower)));
    }

    @Test
    public void testIsFollower() throws ExecutionException, InterruptedException {
        Participant other = new Participant(mockFollower.getUid(), mockFollower.getUsername());
        Tasks.await(database.deleteUser(other).continueWith(task -> database.addUser(other)));
        assertFalse(Tasks.await(database.isFollower(other)));
        DocumentReference otherFollowersPath = FirebaseFirestore.getInstance()
                .collection("users")
                .document(other.getUid())
                .collection("followers")
                .document(auth.getUid());
        Tasks.await(otherFollowersPath.set(new Follower(auth.getUid(), "user")));
        assertTrue(Tasks.await(database.isFollower(other)));
    }

    @Test
    public void testAcceptFollowRequest() throws ExecutionException, InterruptedException {
        database.addFollowRequest(mockFollowRequest);
        assertFalse(Tasks.await(database.getFollowers()).contains(mockFollower));
        assertTrue(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest));
        Tasks.await(database.acceptFollowRequest(mockFollowRequest));
        assertTrue(Tasks.await(database.getFollowers()).contains(mockFollower));
        assertFalse(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest));
    }

    @Test
    public void testDenyFollowRequest() throws ExecutionException, InterruptedException {
        database.addFollowRequest(mockFollowRequest);
        assertFalse(Tasks.await(database.getFollowers()).contains(mockFollower));
        assertTrue(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest));
        Tasks.await(database.denyFollowRequest(mockFollowRequest));
        assertFalse(Tasks.await(database.getFollowers()).contains(mockFollower));
        assertFalse(Tasks.await(database.getFollowRequests()).contains(mockFollowRequest));
    }
}
