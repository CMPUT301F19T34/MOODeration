package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.FollowedMoodEventRepository;

import java.util.HashMap;

/**
 * ViewModel for FollowedMoodsFragment
 */
public class FollowedMoodsViewModel extends ViewModel {
    private FollowedMoodEventRepository repository;

    /**
     * Default constructor. Creates dependencies internally.
     */
    public FollowedMoodsViewModel() {
        this.repository = new FollowedMoodEventRepository();
    }

    /**
     * Constructor with dependency injection.
     * @param repository
     *      The repository to retrieve data from.
     */
    public FollowedMoodsViewModel(FollowedMoodEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Get LiveData referring to the list of mood events of followed participants
     */
    public LiveData<HashMap<String, MoodEvent>> getMoodEvents() {
        return repository.getFollowedMoodEvents();
    }
}