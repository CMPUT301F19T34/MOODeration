package com.example.mooderation.backend;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.EmotionalState;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class MoodHistoryRepositoryTest {
    private static FirebaseFirestore firestore;

    // mock participants used for testing
    private static Participant myParticipant = new Participant("my-uid", "me");

    // mock FirebaseUsers to be be injected into the repository
    @Mock
    FirebaseUser myUser;

    // mock observers -- forces test to wait for LiveData to update
    @Mock
    Observer<List<MoodEvent>> moodHistoryObserver;

    // repositories for testing
    private MoodHistoryRepository myMoodHistoryRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MoodEvent mockMoodEvent() {
        return new MoodEvent(
                new Date(0), // initialized with a fixed time
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "No reason");
    }

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        firestore = FirebaseFirestore.getInstance();

        // create documents for users
        FirebaseUtils.createUser(firestore, myParticipant);
    }

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        // prepare mock FirebaseUsers
        MockitoAnnotations.initMocks(this);
        when(myUser.getUid()).thenReturn(myParticipant.getUid());

        // set up myParticipant follow repository
        myMoodHistoryRepository = new MoodHistoryRepository(myUser, firestore);
        myMoodHistoryRepository.getMoodHistory().observeForever(moodHistoryObserver);
    }

    @Test
    public void testAdd() throws ExecutionException, InterruptedException {
        MoodEvent moodEvent = mockMoodEvent();
        Tasks.await(myMoodHistoryRepository.add(moodEvent));

        // assert the mood event has been added
        LiveData<List<MoodEvent>> moodHistory = myMoodHistoryRepository.getMoodHistory();
        assertNotNull(moodHistory);
        assertEquals(1, moodHistory.getValue().size());
        assertEquals(moodEvent, moodHistory.getValue().get(0));
    }

    @Test
    public void testRemove() throws ExecutionException, InterruptedException {
        MoodEvent moodEvent = mockMoodEvent();
        Tasks.await(myMoodHistoryRepository.add(moodEvent));
        Tasks.await(myMoodHistoryRepository.remove(moodEvent));

        assertNotNull(myMoodHistoryRepository.getMoodHistory());
        assertEquals(0, myMoodHistoryRepository.getMoodHistory().getValue().size());
    }
}
