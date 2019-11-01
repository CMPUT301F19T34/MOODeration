package com.example.mooderation;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.backend.FirebaseDatabase;
import com.example.mooderation.backend.TestDatabaseActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(MockitoJUnitRunner.class)
public class TestDatabase {
    private Solo solo;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseFunctions functions;

    private DocumentReference userPath;
    private CollectionReference followersPath;
    private CollectionReference followRequestsPath;

    @Rule
    public ActivityTestRule<TestDatabaseActivity> rule = new ActivityTestRule<>(TestDatabaseActivity.class, true, true);

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        functions = FirebaseFunctions.getInstance();

        auth.signOut();
        // Thread.sleep(1000);

        Task signIn = auth.signInWithEmailAndPassword("user@example.com", "password");
        Tasks.await(signIn);
        assertTrue(signIn.isSuccessful());

        Tasks.await(deleteAtPath(db.collection("users").document(auth.getUid()).getPath()));
        assertFalse(Tasks.await(db.collection("users").document(auth.getUid()).get()).exists());
        Tasks.await(db.collection("users").document(auth.getUid()).set(makeMap("username", "user")));

        userPath = db.collection("users").document(auth.getUid());
        followersPath = userPath.collection("followers");
        followRequestsPath = userPath.collection("follow_requests");

        Tasks.await(
                db.collection("users")
                        .document(auth.getUid())
                        .collection("follow_requests")
                        .document("follower1")
                        .set(makeMap(
                                "uid", "follower1",
                                "username", "follower1_username",
                                "createTimestamp", Timestamp.now()
                        ))
        );
        Tasks.await(
                db.collection("users")
                        .document(auth.getUid())
                        .collection("follow_requests")
                        .document("follower2")
                        .set(makeMap(
                                "uid", "follower2",
                                "username", "follower2_username",
                                "createTimestamp", Timestamp.now()
                        ))
        );
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testAcceptFollowRequest() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", TestDatabaseActivity.class);

        assertEquals(
                0,
                Tasks.await(followersPath.whereEqualTo("uid", "follower1").get()
        ).size());

        FollowRequest request = Tasks.await(followRequestsPath.whereEqualTo("uid", "follower1").get())
                .iterator().next().toObject(FollowRequest.class);

        new FirebaseDatabase().acceptFollowRequest(request);

        assertEquals(
            1,
            Tasks.await(followersPath.whereEqualTo("uid", "follower1").get()
        ).size());

        assertEquals(
            0,
            Tasks.await(followRequestsPath.whereEqualTo("uid", "follower1").get()
        ).size());
    }

    @Test
    public void testDenyFollowRequest() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", TestDatabaseActivity.class);

        assertEquals(
                0,
                Tasks.await(followersPath.whereEqualTo("uid", "follower1").get()
                ).size());

        Task<QuerySnapshot> followRequestTask = followRequestsPath.whereEqualTo("uid", "follower1").get();
        Tasks.await(followRequestTask);
        FollowRequest request = followRequestTask.getResult().iterator().next().toObject(FollowRequest.class);

        new FirebaseDatabase().denyFollowRequest(request);

        assertEquals(
                0,
                Tasks.await(followersPath.whereEqualTo("uid", "follower1").get()
                ).size());

        assertEquals(
                0,
                Tasks.await(followRequestsPath.whereEqualTo("uid", "follower1").get()
                ).size());
    }

    @Test
    public void testIsAuthenticated() {
        assertTrue(new FirebaseDatabase().authenticated());
    }

    @Test
    public void testIsAuthenticatedAfterSignout() throws InterruptedException {
        auth.signOut();
        // Thread.sleep(500);
        assertFalse(new FirebaseDatabase().authenticated());
    }

    @Test
    public void testAddFollowRequestsListenerInitialCall() throws InterruptedException, ExecutionException {
        solo.assertCurrentActivity("Wrong Activity", TestDatabaseActivity.class);

        AtomicBoolean calledOnce = new AtomicBoolean(false);
        AtomicBoolean calledTwice = new AtomicBoolean(false);

        FirebaseDatabase database = new FirebaseDatabase();
        database.addFollowRequestsListener(requests -> {
            if (!calledOnce.get()) {
                assertEquals(2, requests.size());
                calledOnce.set(true);
            } else {
                assertEquals(1, requests.size());
                calledTwice.set(true);
            }
        });
        Thread.sleep(500);
        assertTrue(calledOnce.get());
        Tasks.await(followRequestsPath.document("follower1").delete());
        Thread.sleep(500);
        assertTrue(calledTwice.get());
    }

    private Map<String, Object> makeMap(Object... args) {
        Map<String, Object> re = new HashMap<>();
        for (int i=0; i<args.length; i += 2) {
            re.put((String) args[i], args[i+1]);
        }
        return re;
    }

    private Task deleteAtPath(String path) {
        HttpsCallableReference function = functions.getHttpsCallable("recursiveDelete");
        return function.call(makeMap("path", path));
    }
}
