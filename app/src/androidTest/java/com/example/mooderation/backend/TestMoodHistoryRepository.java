package com.example.mooderation.backend;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.SocialSituation;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TestMoodHistoryRepository {
    private MoodHistoryRepository moodHistoryRepository;
    private ParticipantRepository participantRepository;

    private CollectionReference moodHistoryPath;
    private MoodEvent mockMoodEvent;
    private Participant p;

    private List<MoodEvent> moodEventList;

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        moodHistoryRepository = new MoodHistoryRepository();
        participantRepository = new ParticipantRepository();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Tasks.await(auth.signInAnonymously());

        DocumentReference userPath = db.collection("users").document(auth.getUid());
        moodHistoryPath = userPath.collection("mood_history");

        mockMoodEvent = new MoodEvent(
                new Date(),
                EmotionalState.HAPPY,
                SocialSituation.ALONE,
                "No reason"
        );
        p = new Participant(auth.getUid(), "user");
        Tasks.await(participantRepository.remove(p).continueWith(task -> participantRepository.add(p)));
    }

    @Test
    public void testAdd() throws ExecutionException, InterruptedException {
        assertEquals(0, Tasks.await(moodHistoryPath.get()).size());
        moodHistoryRepository.add(p, mockMoodEvent);
        assertEquals(mockMoodEvent,
                Tasks.await(moodHistoryPath.get()).iterator().next().toObject(MoodEvent.class));
    }

    @Test
    public void testRemove() throws ExecutionException, InterruptedException {
        moodHistoryRepository.add(p, mockMoodEvent);
        assertEquals(1, Tasks.await(moodHistoryPath.get()).size());

        moodHistoryRepository.remove(p, mockMoodEvent);
        assertEquals(0, Tasks.await(moodHistoryPath.get()).size());
    }
}
