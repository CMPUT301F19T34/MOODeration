package com.example.mooderation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoodEventHistoryTest {
    private static MoodEventHistory moodEventHistory;

    private static MoodEvent mockMoodEvent() {
        MoodEvent moodEvent = new MoodEvent(
                Calendar.getInstance(),
                MoodEvent.EmotionalState.HAPPY
        );
        return moodEvent;
    }

    @BeforeAll
    static void testSetup() {
        moodEventHistory = new MoodEventHistory();

        MoodEvent moodEvent = mockMoodEvent();
        moodEventHistory.addMoodEvent(moodEvent);
    }

    @Test
    void testAddMoodEvent() {
        assertEquals(1, moodEventHistory.getHistory().size());

        final MoodEvent moodEvent = new MoodEvent(
                Calendar.getInstance(),
                MoodEvent.EmotionalState.SAD
        );
        moodEventHistory.addMoodEvent(moodEvent);

        assertEquals(2, moodEventHistory.getHistory().size());

        assertThrows(IllegalArgumentException.class,
                () -> moodEventHistory.addMoodEvent(moodEvent));
    }
}
