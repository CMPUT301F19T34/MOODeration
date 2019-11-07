package com.example.mooderation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnit4.class)
public class TestParticipant {
    private Participant mockParticipant;

    @Before
    public void setUp() {
        mockParticipant = new Participant("uid", "name");
    }

    @Test
    public void testGetUid() {
        assertEquals("uid", mockParticipant.getUid());
    }

    @Test
    public void testGetUsername() {
        assertEquals("name", mockParticipant.getUsername());
    }

    @Test
    public void testEmptyConstructor() {
        Participant participant = new Participant();
        assertEquals("", participant.getUid());
        assertEquals("", participant.getUsername());
    }
}
