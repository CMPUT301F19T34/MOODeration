package com.example.mooderation.backend;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.FollowRequest;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.SocialSituation;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FollowedMoodsRepositoryTest {
    private static FirebaseFirestore firestore;

    // mock participants used for testing
    private static Participant myParticipant = new Participant("my-uid", "me");
    private static Participant otherParticipant = new Participant("other-uid", "other");

    // mock FirebaseUsers to be be injected into the repository
    @Mock
    FirebaseUser myUser;
    @Mock
    FirebaseUser otherUser;

    // repositories for testing
    private MoodEventRepository otherMoodEventRepository;
    private FollowRepository myFollowRepository;
    private FollowRepository otherFollowRepository;
    private FollowedMoodEventRepository myFollowedMoodEventRepository;

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

        // set up myParticipant follow and following repositories
        myFollowRepository = new FollowRepository(myUser, firestore);
        myFollowedMoodEventRepository = new FollowedMoodEventRepository(myUser, firestore);

        // Set up otherParticipant following and mood events repositories
        otherFollowRepository = new FollowRepository(otherUser, firestore);
        otherMoodEventRepository = new MoodEventRepository(otherUser, firestore);
    }

    @Test
    public void testFollow() throws InterruptedException, ExecutionException, ParseException {
        // Set up myParticipant to follow otherParticipant
        Tasks.await(myFollowRepository.follow(otherParticipant));
        LiveData<List<FollowRequest>> followRequestsLiveData = otherFollowRepository.getFollowRequests();
        Thread.sleep(100);
        FollowRequest request = followRequestsLiveData.getValue().get(0);
        Tasks.await(otherFollowRepository.accept(request));

        // otherParticipant makes some mood events
        SimpleDateFormat f = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        MoodEvent firstMoodEvent = new MoodEvent(f.parse("12:00 12-12-1212"), EmotionalState.HAPPY, SocialSituation.ALONE, "aegfdferafsdc", null);
        MoodEvent secondMoodEvent = new MoodEvent(f.parse("13:00 12-12-1212"), EmotionalState.SAD, SocialSituation.CROWD, "adfafrwszd", null);
        otherMoodEventRepository.add(firstMoodEvent);
        otherMoodEventRepository.add(secondMoodEvent);

        // Check that the mood event shows up in the follow repository
        LiveData<HashMap<Participant, MoodEvent>> moodEventHashMapLiveData = myFollowedMoodEventRepository.getFollowedMoodEvents();
        Thread.sleep(100);
        HashMap<Participant, MoodEvent> moodEventHashMap = moodEventHashMapLiveData.getValue();

        assertNotNull(moodEventHashMap);
        assertEquals(1, moodEventHashMap.size());
        assertTrue(moodEventHashMap.keySet().contains(otherParticipant));
        assertEquals(secondMoodEvent, moodEventHashMap.get(otherParticipant));
    }
}
