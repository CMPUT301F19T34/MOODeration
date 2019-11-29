package com.example.mooderation.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.ImageRepository;
import com.example.mooderation.backend.MoodEventRepository;
import com.google.android.gms.tasks.Task;

/**
 * ViewModel for MoodEventFragment
 */
public class MoodEventViewModel extends ViewModel {
    private MoodEventRepository moodEventRepository;
    private ImageRepository imageRepository;

    private MoodEvent moodEvent;
    private MutableLiveData<MoodEvent> moodEventLiveData;
    private MutableLiveData<Boolean> isEditingLiveData;
    private MutableLiveData<Boolean> locationToggleStateLiveData;

    private Uri localUri;

    /**
     * Default constructor. Creates dependencies internally.
     */
    public MoodEventViewModel() {
        moodEventRepository = new MoodEventRepository();
        imageRepository = new ImageRepository();
    }

    /**
     * Constructor with dependency injection.
     * @param moodEventRepository
     *      An instance of MoodEventRepositoty
     * @param imageRepository
     *      An instance of ImageRepository
     */
    public MoodEventViewModel(MoodEventRepository moodEventRepository, ImageRepository imageRepository) {
        this.moodEventRepository = moodEventRepository;
        this.imageRepository = imageRepository;
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

        this.moodEvent = moodEvent;
        moodEventLiveData.setValue(moodEvent);
    }

    /**
     * Updates the mood event being viewed.
     * @param callback
     *      A function that should update the mood event.
     */
    public void updateMoodEvent(UpdateMoodEventCallback callback) {
        MoodEvent moodEvent = callback.update(this.moodEvent);
        this.moodEvent = moodEvent;
        this.moodEventLiveData.setValue(moodEvent);
    }

    /**
     * Get live data of the mood event.
     * @return
     *      Live data tracking the current mood event.
     */
    public LiveData<MoodEvent> getMoodEvent() {
        if (moodEventLiveData == null) {
            throw new IllegalStateException("Mood event cannot be null!");
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
    /**
     * Upload an image to Firebase Storage
     * @param imageUri
     *      The image's URI
     */

    public void uploadImage(Uri imageUri) {
        imageRepository.uploadImage(imageUri).addOnSuccessListener(taskSnapshot -> {
            if (moodEvent == null) {
                throw new IllegalStateException("Mood event cannot be null!");
            }
            moodEvent.setImagePath(imageRepository.getImagePath(imageUri));
            moodEventLiveData.setValue(moodEvent);
        });
    }

    /**
     * Download the image from Firebase storage.
     * @return
     *      An async task.
     */
    public Task<byte[]> downloadImage() {
        return imageRepository.downloadImage(moodEvent.getImagePath());
    }

    /**
     * Delete an image from Firestore Storage.
     */
    public void deleteImage() {
        imageRepository.deleteImage(moodEvent.getImagePath());
        moodEvent.setImagePath(null);
        moodEventLiveData.setValue(moodEvent);
    }

    /**
     * Save the changes made to the current mood event.
     */
    public void saveChanges() {
        if (moodEvent != null) {

            moodEventRepository.add(moodEvent);
        }
    }

    /**
     * Interface for a mood event update callback.
     * Used to make changes to the mood event.
     */
    public interface UpdateMoodEventCallback {
        public MoodEvent update(MoodEvent moodEvent);
    }
}
