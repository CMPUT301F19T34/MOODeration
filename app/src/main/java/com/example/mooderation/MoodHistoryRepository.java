package com.example.mooderation;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Handles locally storing and updating the user's MoodHistory
 * This class is unnecessary at the moment but in the future it will
 * handle the interacting with Firebase
 */
public class MoodHistoryRepository {
    private ArrayList<MoodEvent> moodEventList;

    /**
     * Creates a new MoodHistory object with no MoodEvents
     */
    public MoodHistoryRepository() {
        moodEventList = new ArrayList<>();
    }

    /**
     * Adds a MoodEvent to the MoodHistory
     * Throws IllegalArgumentException if moodEvent is already
     * in MoodHistory
     * @param moodEvent The MoodEvent to add
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        if (moodEventList.contains(moodEvent)) {
            throw new IllegalArgumentException();
        }

        moodEventList.add(moodEvent);
        Collections.sort(moodEventList);    // temporary fix to keep MoodEvents in order
    }

    /**
     * Returns the list of MoodEvents stored in MoodHistory
     * @return ArrayList of MoodEvents.
     */
    public ArrayList<MoodEvent> getMoodEventList() {
        return moodEventList;
    }
}
