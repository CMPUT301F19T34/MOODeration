package com.example.mooderation;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MoodEventTest {
    private Date date;
    private MoodEvent moodEvent;

    @Before
    public void setUp() {
        date = new Date();
        moodEvent = new MoodEvent(
                date,
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "Reason");
    }

    @Test
    public void testGetters() {
        assertEquals(date, moodEvent.getDate());
        assertEquals(EmotionalState.HAPPY, moodEvent.getEmotionalState());
        assertEquals(SocialSituation.NONE, moodEvent.getSocialSituation());
        assertEquals("Reason", moodEvent.getReason());
    }

    @Test
    public void testEquals() {
        MoodEvent mEvent = new MoodEvent(
                date,
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "Reason");

        assertEquals(moodEvent, moodEvent);
        assertEquals(moodEvent, mEvent);

        mEvent = new MoodEvent(
                date,
                EmotionalState.SAD,
                SocialSituation.NONE,
                "Reason");

        assertNotEquals(moodEvent, mEvent);
    }
}
