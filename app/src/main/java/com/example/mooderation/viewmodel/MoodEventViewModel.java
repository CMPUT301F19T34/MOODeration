package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodEventRepository;

/**
 * ViewModel for MoodEventFragment
 */
public class MoodEventViewModel extends ViewModel {
    private MoodEventRepository moodEventRepository;
    private MutableLiveData<MoodEvent> moodEventLiveData;

    /**
     * Default constructor. Creates dependencies internally.
     */
    public MoodEventViewModel() {
        moodEventRepository = new MoodEventRepository();
    }

    /**
     * Constructor with dependency injection.
     * @param moodEventRepository
     */
    public MoodEventViewModel(MoodEventRepository moodEventRepository) {
        this.moodEventRepository = moodEventRepository;
    }

    /**
     * Set the mood event displayed by this fragment.
     * @param moodEvent
     *      The mood event display and edit.
     */
    public void setMoodEvent(MoodEvent moodEvent) {
        // initialize live data
        if (moodEventLiveData == null) {
            moodEventLiveData = new MutableLiveData<>();
        }

        moodEventLiveData.setValue(moodEvent);
    }

    public LiveData<MoodEvent> getMoodEvent() {
        if (moodEventLiveData == null) {
            throw new IllegalStateException("Mood event must be set!");
        }

        return moodEventLiveData;
    }

    // TODO
    public void saveChanges() {
        MoodEvent moodEvent = moodEventLiveData.getValue();
        if (moodEvent != null) {
            moodEventRepository.add(moodEvent);
        }
    }
}
