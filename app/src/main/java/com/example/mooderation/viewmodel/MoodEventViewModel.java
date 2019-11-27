package com.example.mooderation.viewmodel;

import android.net.Uri;
import android.util.Log;

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

    public void updateMoodEvent(UpdateMoodEventCallback callback) {
        MoodEvent moodEvent = callback.update(this.moodEvent);
        this.moodEvent = moodEvent;
        this.moodEventLiveData.setValue(moodEvent);
    }

    public LiveData<MoodEvent> getMoodEvent() {
        if (moodEventLiveData == null) {
            throw new IllegalStateException("Mood event cannot be null!");
        }
        return moodEventLiveData;
    }

    public void uploadImage(Uri imageUri) {
        imageRepository.uploadImage(imageUri).addOnSuccessListener(taskSnapshot -> {
            if (moodEvent == null) {
                throw new IllegalStateException("Mood event cannot be null!");
            }
            Log.d("IMAGE_PATH", taskSnapshot.getStorage().toString());
            moodEvent.setImagePath(imageRepository.getImagePath(imageUri));
            moodEventLiveData.setValue(moodEvent);
        });
    }

    public Task<byte[]> downloadImage() {
        return imageRepository.downloadImage(moodEvent.getImagePath());
    }

    public void deleteImage() {
        imageRepository.deleteImage(moodEvent.getImagePath());
        moodEvent.setImagePath(null);
        moodEventLiveData.setValue(moodEvent);
    }

    // TODO
    public void saveChanges() {
        if (moodEvent != null) {
            moodEventRepository.add(moodEvent);
        }
    }

    public interface UpdateMoodEventCallback {
        public MoodEvent update(MoodEvent moodEvent);
    }
}
