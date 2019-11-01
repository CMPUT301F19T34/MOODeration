package com.example.mooderation;

import android.location.Location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoodHistoryRepositoryTest {
    private static MoodHistoryRepository moodHistory;

    MoodEvent mockMoodEvent() {
        return new MoodEvent(
                Calendar.getInstance(),
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "Reason",
                null);
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

    @Test
    void testGetMoodEventList() {
        for (int i = 0; i < 10; i++) {
            moodHistory.addMoodEvent(mockMoodEvent());
            try {
                // ensures there is difference in time between each MoodEvent
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // ensure the MoodEvent list is in reverse chronological order
        for (int i = 0; i < moodHistory.getMoodEventList().size() - 1; i++) {
            MoodEvent first = moodHistory.getMoodEventList().get(i);
            MoodEvent second = moodHistory.getMoodEventList().get(i + 1);
            assertTrue(first.getCalendar().after(second.getCalendar()));
        }
    }
}
