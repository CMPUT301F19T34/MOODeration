package com.example.mooderation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.backend.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FollowRequestsViewModel extends ViewModel {
    private Database database;
    private MutableLiveData<List<FollowRequest>> followRequests;

    public FollowRequestsViewModel() {
        database = new Database();
        followRequests = new MutableLiveData<>(new ArrayList<>());

        database.addFollowRequestsListener(requests -> {
            requests = new ArrayList<>(requests);
            Collections.sort(requests, (lhs, rhs) -> {
                return -lhs.getCreateTimestamp().compareTo(rhs.getCreateTimestamp());
            });
            followRequests.setValue(requests);
        });
    }

    public Database getDatabase() {
        return database;
    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        return followRequests;
    }
}
