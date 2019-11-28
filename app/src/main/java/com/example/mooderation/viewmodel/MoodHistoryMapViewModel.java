package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodEventRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * View model for the mood history map fragment
 */
public class MoodHistoryMapViewModel extends ViewModel {
    private MoodEventRepository moodEventRepository = new MoodEventRepository();
    private LiveData<List<MoodEvent>> moodEventsWithLocation;

    // TODO implement real dependency injection

    /**
     * Initialize the model with a specific repository object. Used for testing with mock
     * repositories.
     * @param moodEventRepository The repository object to use
     */
    public MoodHistoryMapViewModel(MoodEventRepository moodEventRepository) {
        this.moodEventRepository = moodEventRepository;
    }

    /**
     * Initialize the model with the default repository object, which accesses the database
     */
    public MoodHistoryMapViewModel() {
    }

    /**
     * Gets the mood events to show on the map
     * @return A Livedata pointing to a list of the mood events with non-null location
     */
    public LiveData<List<MoodEvent>> getMoodHistoryWithLocation() {
        if (moodEventsWithLocation == null) {
            moodEventsWithLocation = Transformations.map(this.moodEventRepository.getMoodHistory(),
                    input -> {
                        List<MoodEvent> events = new ArrayList<>();
                        for (MoodEvent event : input) {
                            if (event.getLocation() != null) {
                                events.add(event);
                            }
                        }
                        return events;
                    });
        }
        return moodEventsWithLocation;
    }
}
