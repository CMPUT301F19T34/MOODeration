package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowedMoodEventRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * View model for the followed moods map fragment
 */
public class FollowedMoodHistoryMapViewModel extends ViewModel {
    private FollowedMoodEventRepository followedMoodEventRepository = new FollowedMoodEventRepository();
    private LiveData<HashMap<Participant, MoodEvent>> followedMoodEventsWithLocation;

    // TODO implement real dependency injection

    /**
     * Initialize the model with a specific repository
     * @param followedMoodEventRepository Repository to use
     */
    public FollowedMoodHistoryMapViewModel(FollowedMoodEventRepository followedMoodEventRepository) {
        this.followedMoodEventRepository = followedMoodEventRepository;
    }

    /**
     * Initialize the model with a default repository.
     */
    public FollowedMoodHistoryMapViewModel() {}

    /**
     * Gets the followed mood events to show on the map.
     * @return A LiveData pointing to a hashmap of followed participants to their most recent mood.
     */
    public LiveData<HashMap<Participant, MoodEvent>> getFollowedMoodEventsWithLocation() {
        if (followedMoodEventsWithLocation == null) {
            followedMoodEventsWithLocation = Transformations.map(followedMoodEventRepository.getFollowedMoodEvents(), input -> {
                HashMap<Participant, MoodEvent> events = new HashMap<>();
                for (Map.Entry<Participant, MoodEvent> entry : input.entrySet()) {
                    if (entry.getValue().getLocation() != null) {
                        events.put(entry.getKey(), entry.getValue());
                    }
                }
                return events;
            });
        }
        return followedMoodEventsWithLocation;
    }
}
