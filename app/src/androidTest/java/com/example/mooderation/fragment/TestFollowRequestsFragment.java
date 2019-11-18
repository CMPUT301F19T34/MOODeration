package com.example.mooderation.fragment;

import android.app.Activity;

import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.MainActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

//import com.example.mooderation.ParticipantViewModel;

public class TestFollowRequestsFragment {
    private Solo solo;

//    private FollowRequestRepository followRequestRepository;
//    private FollowRepository followRepository;
//    private ParticipantRepository participantRepository;
//
//    private Participant p;
//    //ParticipantViewModel participantViewModel;
//
//    private FollowRequest mockFollowRequest1;
//    private FollowRequest mockFollowRequest2;
//
//    private Follower mockFollower1 = new Follower("uid1", "name1");
//    private Follower mockFollower2 = new Follower("uid2", "name2");

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        solo = new Solo(getInstrumentation(), rule.getActivity());
//
//        followRepository = new FollowRepository();
//        followRequestRepository = new FollowRequestRepository();
//        participantRepository = new ParticipantRepository();
//
//        mockFollowRequest1 = new FollowRequest("uid1", "name1", Timestamp.now());
//        Thread.sleep(1);
//        mockFollowRequest2 = new FollowRequest("uid2", "name2", Timestamp.now());
//
//        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());
//        rule.launchActivity(new Intent());
//
//        // create mock participant
//        p = new Participant(FirebaseAuth.getInstance().getUid(), "user");

        // register the participant to the view model
        //participantViewModel = ViewModelProviders.of(rule.getActivity()).get(ParticipantViewModel.class);
        //participantViewModel.setParticipant(p);

//        Tasks.await(participantRepository.remove(p).continueWith(task -> participantRepository.register(p)));
//        Tasks.await(followRequestRepository.add(p, mockFollowRequest1)
//                .continueWithTask(task -> followRequestRepository.add(p, mockFollowRequest2)));

    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

//    @Test
//    public void testAcceptFollower() throws ExecutionException, InterruptedException {
//        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//
//        navigateToFollowRequestFragment();
//
//        onData(anything()).inAdapterView(withId(R.id.follow_request_list)).atPosition(0).perform(click());
//        onView(withText("Accept")).perform(click());
//        assertTrue(contains(followRepository, p, mockFollower2));
//        assertFalse(contains(followRequestRepository, p, mockFollowRequest2));
//    }
//
//    @Test
//    public void testDenyFollower() throws ExecutionException, InterruptedException {
//        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//
//        navigateToFollowRequestFragment();
//
//        onData(anything()).inAdapterView(withId(R.id.follow_request_list)).atPosition(0).perform(click());
//        onView(withText("Deny")).perform(click());
//        assertFalse(contains(followRepository, p, mockFollower2));
//        assertFalse(contains(followRequestRepository, p, mockFollowRequest2));
//    }
//
//    private <Owner, Item> boolean contains(OwnedRepository<Owner, Item> repo, Owner owner, Item item) throws ExecutionException, InterruptedException {
//        TaskCompletionSource<List<Item>> source = new TaskCompletionSource<>();
//        ListenerRegistration reg;
//        reg = repo.addListener(owner, items -> {
//            source.setResult(items);
//        });
//        boolean result = Tasks.await(source.getTask()).contains(item);
//        reg.remove();
//        return result;
//    }
//
//    /**
//     * Opens the navigation drawer.
//     * Used in place of solo.setNavigationDrawer(Solo.OPENED);
//     * https://gist.github.com/quentin7b/9b51a3827c842417636b
//     */
//    private void openNavigationDrawer() {
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
//            }
//        });
//    }
//
//    /**
//     * Navigates to the follow request fragment
//     */
//    private void navigateToFollowRequestFragment() {
//        // open nav drawer
//        openNavigationDrawer();
//
//        // get follow request item
//        String followRequestLabel = rule.getActivity().getString(R.string.follow_request_label);
//
//        // navigate to follow request
//        assertTrue(solo.waitForText(followRequestLabel, 1, 2000));
//        solo.clickOnText(followRequestLabel);
//    }
}
