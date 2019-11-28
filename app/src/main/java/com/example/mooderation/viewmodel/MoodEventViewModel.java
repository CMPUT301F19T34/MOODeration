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
    private MutableLiveData<Boolean> isEditingLiveData;
    private MutableLiveData<Boolean> locationToggleStateLiveData;

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


    /**
     * Sets isEditing which indicates whether the moodEvent is being created or edited
     * @param isEditing
     */
    public void setIsEditing(Boolean isEditing) {
        // Initialize live data
        if (isEditingLiveData == null) {
            isEditingLiveData = new MutableLiveData<>();
        }

        isEditingLiveData.setValue(isEditing);
    }

    /**
     * Returns a boolean which indicates if the moodEvent is being created or edited
     * @return isEditingLiveData
     */
    public LiveData<Boolean> getIsEditing() {
        if (isEditingLiveData == null) {
            setIsEditing(false);
        }

        return isEditingLiveData;
    }

    /**
     * Sets a boolean which indicates the correct state of the location toggle
     * @param locationToggleState
     */
    public void setLocationToggleState(Boolean locationToggleState) {
        // Initialize live data
        if (locationToggleStateLiveData == null) {
            locationToggleStateLiveData = new MutableLiveData<>();
        }

        locationToggleStateLiveData.setValue(locationToggleState);
    }

    /**
     * Returns a boolean which indicates the correct state of the location toggle
     * @return locationToggleStateLiveData
     */
    public LiveData<Boolean> getLocationToggleState() {
        if (locationToggleStateLiveData == null) {
            setLocationToggleState(false);
        }

        return locationToggleStateLiveData;
    }

    // TODO
    public void saveChanges() {
        MoodEvent moodEvent = moodEventLiveData.getValue();
        if (moodEvent != null) {

            moodEventRepository.add(moodEvent);
        }
    }
}
