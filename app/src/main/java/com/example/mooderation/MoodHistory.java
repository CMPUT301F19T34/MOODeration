package com.example.mooderation;

import java.util.ArrayList;

/**
 * Store's a user MoodEvent history
 */
public class MoodHistory {
    private ArrayList<MoodEvent> history;

    /**
     * MoodHistory Constructor
     */
    public MoodHistory() {
        history = new ArrayList<>();
    }

    /**
     * Add a new MoodEvent to the MoodHistory
     * @param moodEvent
     *      The new MoodEvent to add to the history
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        if (history.contains(moodEvent)) {
            throw new IllegalArgumentException();
        }

        history.add(moodEvent);
    }

    /**
     * Get a list of MoodEvents
     * @return
     *      The stored list of MoodEvents
     */
    public ArrayList<MoodEvent> getHistory() {
        return history;
    }
}
