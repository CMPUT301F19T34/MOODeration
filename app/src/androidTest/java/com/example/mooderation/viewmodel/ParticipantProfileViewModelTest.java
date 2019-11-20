package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParticipantProfileViewModelTest {
    @Mock
    FollowRepository followRepository;
    @Mock
    Observer<Boolean> booleanObserver;

    private MutableLiveData<Boolean> booleanResult = new MutableLiveData<>(true);

    private Participant viewingParticipant = new Participant("viewing-uid", "username");
    private ParticipantProfileViewModel participantProfileViewModel;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(followRepository.isFollowing(any())).thenReturn(booleanResult);
        when(followRepository.isRequestSent(any())).thenReturn(booleanResult);

        participantProfileViewModel = new ParticipantProfileViewModel(followRepository);
        participantProfileViewModel.setViewingParticipant(viewingParticipant);
        participantProfileViewModel.getFollowRequestSent().observeForever(booleanObserver);
        participantProfileViewModel.getThisFollowingOther().observeForever(booleanObserver);
    }

    @Test
    public void testGetFollowRequestSent() {
        booleanResult.setValue(true);
        assertTrue(participantProfileViewModel.getFollowRequestSent().getValue());

        booleanResult.setValue(false);
        assertFalse(participantProfileViewModel.getFollowRequestSent().getValue());
    }

    @Test
    public void testGetThisFollowingOther() {
        booleanResult.setValue(true);
        assertTrue(participantProfileViewModel.getThisFollowingOther().getValue());

        booleanResult.setValue(false);
        assertFalse(participantProfileViewModel.getThisFollowingOther().getValue());
    }

    @Test
    public void testGetUsername() {
        assertEquals(viewingParticipant.getUsername(), participantProfileViewModel.getUsername().getValue());
    }

    @Test
    public void testSendFollowRequest() {
        participantProfileViewModel.sendFollowRequest();
        verify(followRepository).follow(viewingParticipant);
    }
}
