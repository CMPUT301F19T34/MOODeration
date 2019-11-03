package com.example.mooderation;

import com.example.mooderation.backend.Database;
import com.google.android.gms.tasks.Task;
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

import static com.google.firebase.firestore.util.Assert.fail;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(JUnit4.class)
public class TestDatabase {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private DocumentReference userPath;
    private CollectionReference followersPath;
    private CollectionReference followRequestsPath;

    private FollowRequest mockFollowRequest;
    private FollowRequest mockFollowRequestWithDifferentName;
    private Follower mockFollower;
    private Follower mockFollowerWithDifferentName;


    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        auth.signOut();

        Database database = new Database();
        Tasks.await(auth.signInWithEmailAndPassword("user@example.com", "password")
                .continueWithTask(task -> database.addUser(auth.getUid(), "user"))
                .addOnFailureListener(e -> fail(e.toString())));

        userPath = db.collection("users").document(auth.getUid());
        followersPath = userPath.collection("followers");
        followRequestsPath = userPath.collection("follow_requests");

        mockFollowRequest = new FollowRequest("follower1", "follower1_username", Timestamp.now());
        mockFollowRequestWithDifferentName = new FollowRequest("follower1", "different_name", Timestamp.now());
        mockFollower = new Follower("follower1", "follower1_username");
        mockFollowerWithDifferentName = new Follower("follower1", "different_name");

        Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).delete());
        Tasks.await(followersPath.document(mockFollower.getUid()).delete());
    }


    @Test
    public void testIsAuthenticated() {
        assertTrue(new Database().authenticated());
    }

    @Test
    public void testIsAuthenticatedAfterSignout() throws InterruptedException {
        auth.signOut();
        assertFalse(new Database().authenticated());
    }

    @Test
    public void testGetFollowRequests() throws ExecutionException, InterruptedException {
        Database database = new Database();
        Task<Void> tasks = database.getFollowRequests()
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollowRequest));
                    return null;
                })
                .continueWithTask(task -> followRequestsPath.document(mockFollowRequest.getUid()).set(mockFollowRequest))
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollowRequest));
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }

    @Test
    public void testAddFollowRequest() throws ExecutionException, InterruptedException {
        Database database = new Database();
        Task<Void> tasks = database.getFollowRequests()
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollowRequest));
                    return null;
                })
                .continueWithTask(task -> database.addFollowRequest(mockFollowRequest))
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollowRequest));
                    return null;
                })
                // Idempotence
                .continueWithTask(task -> database.addFollowRequest(mockFollowRequest))
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollowRequest));
                    return null;
                })
                // Overwriting
                .continueWithTask(task -> database.addFollowRequest(mockFollowRequestWithDifferentName))
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    task.getResult().contains(mockFollowRequestWithDifferentName);
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }

    @Test
    public void testDeleteFollowRequest() throws ExecutionException, InterruptedException {
        Database database = new Database();
        Task<Void> tasks = database.addFollowRequest(mockFollowRequest)
                .continueWithTask(task -> database.deleteFollowRequest(mockFollowRequest))
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollowRequest));
                    return null;
                })
                // Idempotence
                .continueWithTask(task -> database.deleteFollowRequest(mockFollowRequest))
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollowRequest));
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }

    @Test
    public void testGetFollowers() throws ExecutionException, InterruptedException {
        Database database = new Database();
        Task<Void> tasks = database.getFollowers()
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollower));
                    return null;
                })
                .continueWithTask(task -> followersPath.document(mockFollower.getUid()).set(mockFollower))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollower));
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }

    @Test
    public void testAddFollower() throws ExecutionException, InterruptedException {
        Database database = new Database();
        Task<Void> tasks = database.getFollowers()
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollower));
                    return null;
                })
                .continueWithTask(task -> database.addFollower(mockFollower))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollower));
                    return null;
                })
                // Idempotence
                .continueWithTask(task -> database.addFollower(mockFollower))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollower));
                    return null;
                })
                // Overwriting
                .continueWithTask(task -> database.addFollower(mockFollowerWithDifferentName))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollowerWithDifferentName));
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }

    @Test
    public void testDeleteFollower() throws ExecutionException, InterruptedException {
        Database database = new Database();
        Task<Void> tasks = database.addFollower(mockFollower)
                .continueWithTask(task -> database.deleteFollower(mockFollower))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollower));
                    return null;
                })
                // Idempotence
                .continueWithTask(task -> database.deleteFollower(mockFollower))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollower));
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }

    @Test
    public void testAcceptFollowRequest() throws Exception {
        Database database = new Database();
        Task<Void> tasks = database.addFollowRequest(mockFollowRequest)
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollower));
                    return null;
                })
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollowRequest));
                    return null;
                })
                .continueWithTask(task -> database.acceptFollowRequest(mockFollowRequest))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollower));
                    return null;
                })
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollowRequest));
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }

    @Test
    public void testDenyFollowRequest() throws Exception {
        Database database = new Database();
        Task<Void> tasks = database.addFollowRequest(mockFollowRequest)
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollower));
                    return null;
                })
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertTrue(task.getResult().contains(mockFollowRequest));
                    return null;
                })
                .continueWithTask(task -> database.denyFollowRequest(mockFollowRequest))
                .continueWithTask(task -> database.getFollowers())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollower));
                    return null;
                })
                .continueWithTask(task -> database.getFollowRequests())
                .continueWith(task -> {
                    assertFalse(task.getResult().contains(mockFollowRequest));
                    return null;
                });
        Tasks.await(tasks);
        assertTrue(tasks.isSuccessful());
    }
}
