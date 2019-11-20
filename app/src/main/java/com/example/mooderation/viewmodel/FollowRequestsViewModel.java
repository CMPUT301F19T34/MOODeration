package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.backend.FollowRepository;

import java.util.List;

public class FollowRequestsViewModel extends ViewModel {
    private FollowRepository followRepository;

    public FollowRequestsViewModel() {
        this.followRepository = new FollowRepository();
    }

    // TODO implement real dependency injection
    public FollowRequestsViewModel(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void acceptRequest(FollowRequest request) {
        followRepository.accept(request);
    }

    public void denyRequest(FollowRequest request) {
        followRepository.deny(request);
    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        return followRepository.getFollowRequests();
    }
}
