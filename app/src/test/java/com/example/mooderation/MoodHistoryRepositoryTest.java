package com.example.mooderation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoodHistoryRepositoryTest {
    private static MoodHistoryRepository moodHistory;

    MoodEvent mockMoodEvent() {
        return new MoodEvent(
                Calendar.getInstance(),
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "Reason");
    }

    @BeforeEach
    void setup() {
        moodHistory = new MoodHistoryRepository();
    }

    @Test
    void testAddMoodEvent() {
        assertEquals(0, moodHistory.getMoodEventList().size());

        MoodEvent moodEvent = mockMoodEvent();
        moodHistory.addMoodEvent(moodEvent);

        assertEquals(1, moodHistory.getMoodEventList().size());
        assertTrue(moodHistory.getMoodEventList().contains(moodEvent));
    }
}
