package com.example.mooderation;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 */
public class MoodHistoryViewModel extends ViewModel {
    private ArrayList<MoodEvent> moodHistory;

    public MoodHistoryViewModel() {
        moodHistory = new ArrayList<>();
    }

    public void addMoodEvent(MoodEvent moodEvent) {
        moodHistory.add(moodEvent);
    }

    public ArrayList<MoodEvent> getMoodHistory() {
        return moodHistory;
    }
}
