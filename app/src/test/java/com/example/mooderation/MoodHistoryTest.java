package com.example.mooderation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoodHistoryTest {
    private static MoodHistory moodHistory;

    private static MoodEvent mockMoodEvent() {
        MoodEvent moodEvent = new MoodEvent(
                Calendar.getInstance(),
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "Reason"
        );
        return moodEvent;
    }

    @BeforeAll
    static void testSetup() {
        moodHistory = new MoodHistory();

        MoodEvent moodEvent = mockMoodEvent();
        moodHistory.addMoodEvent(moodEvent);
    }

    @Test
    void testAddMoodEvent() {
        assertEquals(1, moodHistory.getHistory().size());

        final MoodEvent moodEvent = new MoodEvent(
                Calendar.getInstance(),
                EmotionalState.SAD,
                SocialSituation.NONE,
                "Reason"
        );
        moodHistory.addMoodEvent(moodEvent);

        assertEquals(2, moodHistory.getHistory().size());

        assertThrows(IllegalArgumentException.class,
                () -> moodHistory.addMoodEvent(moodEvent));
    }
}
