package com.example.mooderation;

import com.example.mooderation.viewmodel.ParticipantViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ParticipantViewModelTest {
    private ParticipantViewModel participantViewModel;

    public Participant mockParticipant() {
        return new Participant("uid", "user");
    }

    @Before
    public void setUp() throws Exception {
        participantViewModel = new ParticipantViewModel();
    }

    @Test
    public void testSetParticipant() {
        Participant participant = mockParticipant();

        participantViewModel.setParticipant(participant);
        assertEquals(participant, participantViewModel.getParticipant());
    }

    @Test
    public void testGetParticipant() {
        participantViewModel.setParticipant(mockParticipant());
        Participant participant = participantViewModel.getParticipant();

        assertEquals("uid", participant.getUid());
        assertEquals("user", participant.getUsername());
    }

    @Test(expected = RuntimeException.class)
    public void testGetParticipantException() {
        Participant participant = participantViewModel.getParticipant();
    }
}
