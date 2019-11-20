package com.example.mooderation.backend;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.mooderation.Participant;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class ParticipantRepositoryTest {
    private static FirebaseFirestore firestore;

    // mock observers -- forces test to wait for LiveData to update
    @Mock
    Observer<List<Participant>> participantObserver;

    // repositories for testing
    private ParticipantRepository myParticipantRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() throws ExecutionException, InterruptedException {
        firestore = FirebaseFirestore.getInstance();

        // create documents for users
        // FirebaseUtils.createUser(firestore, myParticipant);
    }

    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        // prepare mocks
        MockitoAnnotations.initMocks(this);

        // set up myParticipant follow repository
        myParticipantRepository = new ParticipantRepository(firestore);
        myParticipantRepository.getParticipants().observeForever(participantObserver);
    }

    @Test
    public void testGetParticipants() throws ExecutionException, InterruptedException {
        Participant participant = new Participant("my-uid", "me");
        FirebaseUtils.createUser(firestore, participant);

        assertNotNull(myParticipantRepository.getParticipants());
        assertTrue(myParticipantRepository.getParticipants().getValue().contains(participant));
    }
}
