package com.example.mooderation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 *
 */
public class MoodHistoryViewModel extends ViewModel {
    private ArrayList<MoodEvent> moodHistory;
    private MutableLiveData<ArrayList<MoodEvent>> moodHistoryLiveData;

    public MoodHistoryViewModel() {
        moodHistory = new ArrayList<>();
        moodHistoryLiveData = new MutableLiveData<>();
    }

    /**
     *
     * @param moodEvent
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        moodHistory.add(moodEvent);
        moodHistoryLiveData.setValue(moodHistory);
    }

    /**
     *
     * @return
     */
    public LiveData<ArrayList<MoodEvent>> getLiveData() {
        return moodHistoryLiveData;
    }

    /**
     *
     * @return
     */
    public ArrayList<MoodEvent> getMoodHistory() {
        return moodHistory;
    }
}