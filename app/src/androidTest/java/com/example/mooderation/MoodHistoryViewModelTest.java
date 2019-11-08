package com.example.mooderation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MoodHistoryViewModelTest {
    private MoodHistoryViewModel moodHistoryViewModel;

    private MoodEvent mockMoodEvent() {
        return new MoodEvent(
                new Date(),
                EmotionalState.HAPPY,
                SocialSituation.NONE,
                "Reason");
    }

    @Before
    public void setUp() throws Exception {
        moodHistoryViewModel = new MoodHistoryViewModel();
    }

    // TODO fill out unit tests.
    @Test
    public void dummyTest() {
        assertEquals(0, 1);
    }
}
