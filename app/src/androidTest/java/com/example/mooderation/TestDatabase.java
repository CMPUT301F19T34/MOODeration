package com.example.mooderation;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.backend.Database;
import com.example.mooderation.backend.TestDatabaseActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ExecutionException;

import static com.google.firebase.firestore.util.Assert.fail;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(MockitoJUnitRunner.class)
public class TestDatabase {
    private Solo solo;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private DocumentReference userPath;
    private CollectionReference followersPath;
    private CollectionReference followRequestsPath;

    private FollowRequest mockFollowRequest;
    private Follower mockFollower;

    @Rule
    public ActivityTestRule<TestDatabaseActivity> rule = new ActivityTestRule<>(TestDatabaseActivity.class, true, true);

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

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
        mockFollower = new Follower("follower1", "follower1_username");

        Tasks.await(followRequestsPath.document(mockFollowRequest.getUid()).delete());
        Tasks.await(followersPath.document(mockFollower.getUid()).delete());

    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
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
                });
        Tasks.await(tasks);
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
                });
        Tasks.await(tasks);
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
                });
        Tasks.await(tasks);
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
                });
        Tasks.await(tasks);
    }

    @Test
    public void testAcceptFollowRequest() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", TestDatabaseActivity.class);

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
    }

    @Test
    public void testDenyFollowRequest() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", TestDatabaseActivity.class);

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
    }

    @Test
    public void testAddFollowRequestsListenerInitialCall() throws InterruptedException, ExecutionException {
        solo.assertCurrentActivity("Wrong Activity", TestDatabaseActivity.class);

//        AtomicBoolean calledOnce = new AtomicBoolean(false);
//        AtomicBoolean calledTwice = new AtomicBoolean(false);
//
//        Database database = new Database();
//        database.addFollowRequestsListener(requests -> {
//            if (!calledOnce.get()) {
//                assertFalse(requests.contains(mockFollowRequest));
//                calledOnce.set(true);
//            } else {
//                assertTrue(requests.contains(mockFollowRequest));
//                calledTwice.set(true);
//            }
//        });
//        Thread.sleep(500);
//        assertTrue(calledOnce.get());
//        Tasks.await(database.addFollowRequest(mockFollowRequest));
//        Thread.sleep(500);
//        assertTrue(calledTwice.get());
    }
}
