package com.example.mooderation;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.mooderation.backend.ParticipantRepository;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MoodHistoryViewModelTest {
    private MoodHistoryViewModel moodHistoryViewModel;
    @Mock Observer<List<MoodEvent>> observer;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private MoodEvent mockMoodEvent() {
        return new MoodEvent(
                new Date(),
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "Reason");
    }

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        // sign in as an anonymous user
        Tasks.await(FirebaseAuth.getInstance().signInAnonymously());
        Participant participant = new Participant(
                FirebaseAuth.getInstance().getUid(), "user");

        MockitoAnnotations.initMocks(this);

        // register the participant to the view model
        moodHistoryViewModel = new MoodHistoryViewModel();
        moodHistoryViewModel.getMoodHistory().observeForever(observer);

        ParticipantRepository participantRepository = new ParticipantRepository();
        Tasks.await(participantRepository.remove(participant).continueWith(
                task -> participantRepository.register(participant)));
    }

    @Test
    public void testAddMoodEvent() throws ExecutionException, InterruptedException {
        MoodEvent moodEvent = mockMoodEvent();

        Tasks.await(moodHistoryViewModel.addMoodEvent(moodEvent));
        assertEquals(moodEvent, moodHistoryViewModel.getMoodHistory().getValue().get(0));
    }

    @Test
    public void testDeleteMoodEvent() throws ExecutionException, InterruptedException {
        MoodEvent moodEvent = mockMoodEvent();

        Tasks.await(moodHistoryViewModel.addMoodEvent(moodEvent));
        assertEquals(1, moodHistoryViewModel.getMoodHistory().getValue().size());

        Tasks.await(moodHistoryViewModel.removeMoodEvent(moodEvent));
        assertEquals(0, moodHistoryViewModel.getMoodHistory().getValue().size());
    }

    @Test
    public void testOrder() throws ExecutionException, InterruptedException {
        // register mood events in chronological order
        Tasks.await(moodHistoryViewModel.addMoodEvent(mockMoodEvent()));
        Thread.sleep(10);
        Tasks.await(moodHistoryViewModel.addMoodEvent(mockMoodEvent()));

        // check for reverse chronological order
        MoodEvent l = moodHistoryViewModel.getMoodHistory().getValue().get(0);
        MoodEvent r = moodHistoryViewModel.getMoodHistory().getValue().get(1);
        assertTrue(l.getDate().compareTo(r.getDate()) > 0);
    }
}
