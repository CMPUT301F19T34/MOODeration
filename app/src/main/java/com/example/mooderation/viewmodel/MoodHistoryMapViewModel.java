package com.example.mooderation.viewmodel;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodEventRepository;

import java.util.ArrayList;
import java.util.List;

public class MoodHistoryMapViewModel extends ViewModel {
    private MoodEventRepository moodEventRepository = new MoodEventRepository();
    private LiveData<List<MoodEvent>> moodEventsWithLocation;

    // TODO implement real dependency injection
    public MoodHistoryMapViewModel(MoodEventRepository moodEventRepository) {
        this.moodEventRepository = moodEventRepository;
    }

    public MoodHistoryMapViewModel() {
    }

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
