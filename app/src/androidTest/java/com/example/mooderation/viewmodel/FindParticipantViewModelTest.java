package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;

public class FindParticipantViewModelTest {
    @Mock
    FirebaseUser user;
    @Mock
    ParticipantRepository participantRepository;
    @Mock
    Observer<List<Participant>> searchObserver;

    private Participant myParticipant = new Participant("my-uid", "username");
    private FindParticipantViewModel findParticipantViewModel;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private List<Participant> mockParticipants() {
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant("uid-1", "albert"));
        participants.add(new Participant("uid-2", "john"));
        participants.add(myParticipant);
        return participants;
    }

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        when(user.getUid()).thenReturn("my-uid");

        findParticipantViewModel = new FindParticipantViewModel(participantRepository, user);

        when(participantRepository.getParticipants()).thenReturn(
                new MutableLiveData<>(mockParticipants()));

        findParticipantViewModel.getSearchResults().observeForever(searchObserver);
    }

    @Test
    public void testSearchHidesCurrentUser() {

        assertNotNull(findParticipantViewModel.getSearchResults().getValue());
        assertFalse(findParticipantViewModel.getSearchResults().getValue().contains(myParticipant));
    }

    @Test
    public void testSearchQuery() {
        findParticipantViewModel.searchFor("al");

        assertNotNull(findParticipantViewModel.getSearchResults().getValue());
        assertEquals(1, findParticipantViewModel.getSearchResults().getValue().size());
    }
}
