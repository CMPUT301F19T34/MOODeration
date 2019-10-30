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
    private MoodHistoryRepository moodHistory;
    private MutableLiveData<MoodHistoryRepository> moodHistoryLiveData;

    /**
     * Default constructor
     * Used by ViewModelProviders
     * TODO implement dependency injection in the future
     */
    public MoodHistoryViewModel() {
        this.moodHistory = new MoodHistoryRepository();
        moodHistoryLiveData = new MutableLiveData<>();
    }

    /**
     * Add a new MoodEvent to the MoodHistory
     * @param moodEvent The MoodEvent to add
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        moodHistory.addMoodEvent(moodEvent);
        moodHistoryLiveData.setValue(moodHistory);
    }

    /**
     * Get the MoodHistory LiveData object
     * @return The LiveData object tracking MoodHistory
     */
    public LiveData<MoodHistoryRepository> getLiveData() {
        return moodHistoryLiveData;
    }

    /**
     * Get the list of MoodEvents from the ViewModel
     * @return The list of MoodEvents stored in MoodHistoryRepository
     */
    public ArrayList<MoodEvent> getMoodHistory() {
        return moodHistory.getMoodEventList();
    }
}