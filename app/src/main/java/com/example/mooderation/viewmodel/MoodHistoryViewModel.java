package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodEventRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for MoodHistoryFragment
 */
public class MoodHistoryViewModel extends ViewModel {
    private MoodEventRepository moodEventRepository = new MoodEventRepository();

    private MutableLiveData<EmotionalState> moodFilter = new MutableLiveData<>(null);
    private LiveData<List<MoodEvent>> moodHistory;

    /**
     * Default constructor. Creates dependencies internally.
     */
    public MoodHistoryViewModel() {
        this.moodEventRepository = new MoodEventRepository();
    }

    /**
     * Constructor with dependency injection.
     * @param moodEventRepository
     *      The repository to retrieve data from.
     */
    public MoodHistoryViewModel(MoodEventRepository moodEventRepository) {
        this.moodEventRepository = moodEventRepository;
    }

    /**
     * Get the current user's mood history.
     * Only returns mood events that match the filter.
     * @return
     *      LiveData tracking a user's mood history.
     */
    public LiveData<List<MoodEvent>> getMoodHistory() {
        if (moodHistory == null) {
            moodHistory = Transformations.switchMap(moodFilter, filter ->
                Transformations.map(moodEventRepository.getMoodHistory(), moodEvents -> {
                    if (filter == null) {
                        return moodEvents;
                    }
                    List<MoodEvent> filteredMoodEvents = new ArrayList<>();
                    for (MoodEvent moodEvent : moodEvents) {
                        if (moodEvent.getEmotionalState() == filter) {
                            filteredMoodEvents.add(moodEvent);
                        }
                    }
                    return filteredMoodEvents;
                }));
        }

        return moodHistory;
    }

    /**
     * Set the new mood event filter.
     * @param emotionalState
     *      The new emotional state to filter for.
     */
    public void setFilter(EmotionalState emotionalState) {
        moodFilter.setValue(emotionalState);
    }

    /**
     * Called when a user adds a mood event.
     * @param moodEvent
     *      The mood event to add.
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        moodEventRepository.add(moodEvent);
    }

    /**
     * Called when a user removes a mood event.
     * @param moodEvent
     *      The mood event to remove.
     */
    public void removeMoodEvent(MoodEvent moodEvent) {
        moodEventRepository.remove(moodEvent);
    }
}