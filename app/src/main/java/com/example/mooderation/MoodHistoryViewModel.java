package com.example.mooderation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * ViewModel for sharing the MoodHistory data between the MoodHistoryFragment
 * and the AddMoodEventFragment
 */
public class MoodHistoryViewModel extends ViewModel {
    private ArrayList<MoodEvent> moodHistory;
    private MutableLiveData<ArrayList<MoodEvent>> moodHistoryLiveData;

    /**
     * Default constructor
     * Used by ViewModelProviders
     */
    public MoodHistoryViewModel() {
        this.moodHistory = new ArrayList<>();
        moodHistoryLiveData = new MutableLiveData<>();
    }

    /**
     * MoodHistoryViewModel Constructor
     * Dependency injection for ease of testing.
     * @param moodHistory The list of MoodEvents to share between fragments
     */
    public MoodHistoryViewModel(ArrayList<MoodEvent> moodHistory) {
        this.moodHistory = moodHistory;
        moodHistoryLiveData = new MutableLiveData<>();
    }

    /**
     * Add a new MoodEvent to the MoodHistory
     * @param moodEvent The MoodEvent to add
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        moodHistory.add(moodEvent);
        moodHistoryLiveData.setValue(moodHistory);
    }

    /**
     * Get the MoodHistory LiveData object
     * @return The LiveData object tracking MoodHistory
     */
    public LiveData<ArrayList<MoodEvent>> getLiveData() {
        return moodHistoryLiveData;
    }

    /**
     * Get the list of
     * @return
     */
    public ArrayList<MoodEvent> getMoodHistory() {
        return moodHistory;
    }
}