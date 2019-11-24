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

    // TODO Javadoc -- implementation will be updated in the near future.

    public void setMoodEvent(MoodEvent moodEvent) {
        if (moodEventLiveData == null) {
            moodEventLiveData = new MutableLiveData<>();
        }
        moodEventLiveData.setValue(moodEvent);
    }

    public LiveData<MoodEvent> getMoodEvent() {
        if (moodEventLiveData == null) {
            throw new IllegalStateException("MoodEvent must be set!");
        }
        return moodEventLiveData;
    }

    public void saveChanges() {
        if (moodEventLiveData == null) {
            throw new IllegalStateException("MoodEvent must be set!");
        }
        moodEventRepository.add(moodEventLiveData.getValue());
    }
}
