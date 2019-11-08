package com.example.mooderation;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.mooderation.backend.MoodHistoryRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MoodHistoryViewModelTest {
    private boolean observed = false;

    private Participant participant;
    private ParticipantRepository participantRepository;
    private MoodHistoryRepository moodHistoryRepository;

    private MoodHistoryViewModel moodHistoryViewModel;

    // forces tasks to execute synchronously
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
        participant = new Participant(FirebaseAuth.getInstance().getUid(), "user");

        participantRepository = new ParticipantRepository();
        moodHistoryRepository = new MoodHistoryRepository();

        // add the participant to the view model
        moodHistoryViewModel = new MoodHistoryViewModel();
        moodHistoryViewModel.setParticipant(participant);

        Tasks.await(participantRepository.remove(participant).continueWith(task -> participantRepository.add(participant)));
    }

    @Test
    public void testAddMoodEvent() throws ExecutionException, InterruptedException {
        MoodEvent moodEvent = mockMoodEvent();
        moodHistoryViewModel.addMoodEvent(moodEvent);

        // TODO fix this hack
        Thread.sleep(1000);

        // check mood event was added
        assertEquals(moodEvent, moodHistoryViewModel.getMoodHistory().getValue().get(0));
    }

    @Test
    public void testOrder() throws ExecutionException, InterruptedException {
        // add mood events in chronological order
        Tasks.await(moodHistoryRepository.add(participant, mockMoodEvent()));
        Thread.sleep(10);
        Tasks.await(moodHistoryRepository.add(participant, mockMoodEvent()));

        // check for reverse chronological order
        MoodEvent l = moodHistoryViewModel.getMoodHistory().getValue().get(0);
        MoodEvent r = moodHistoryViewModel.getMoodHistory().getValue().get(1);
        assertTrue(l.getDate().compareTo(r.getDate()) > 0);
    }

    @Test
    public void testObserver() throws ExecutionException, InterruptedException {
        moodHistoryViewModel.getMoodHistory().observeForever(new Observer<List<MoodEvent>>() {
            @Override
            public void onChanged(List<MoodEvent> moodEvents) {
                observed = true;
            }
        });

        Tasks.await(moodHistoryRepository.add(participant, mockMoodEvent()));
        assertTrue(observed);
    }
}
