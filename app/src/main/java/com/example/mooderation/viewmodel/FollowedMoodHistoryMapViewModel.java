package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowedMoodEventRepository;

import java.util.HashMap;
import java.util.Map;

public class FollowedMoodHistoryMapViewModel extends ViewModel {
    private FollowedMoodEventRepository followedMoodEventRepository = new FollowedMoodEventRepository();
    private LiveData<HashMap<Participant, MoodEvent>> followedMoodEventsWithLocation;

    // TODO implement real dependency injection
    public FollowedMoodHistoryMapViewModel(FollowedMoodEventRepository followedMoodEventRepository) {
        this.followedMoodEventRepository = followedMoodEventRepository;
    }

    public FollowedMoodHistoryMapViewModel() {}

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
