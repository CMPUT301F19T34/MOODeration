package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.backend.FollowRepository;

import java.util.List;

/**
 * ViewModel for FollowRequestFragment
 */
public class FollowRequestsViewModel extends ViewModel {
    private FollowRepository followRepository;

    /**
     * Default constructor. Creates dependencies internally.
     */
    public FollowRequestsViewModel() {
        this.followRepository = new FollowRepository();
    }

    /**
     * Constructor with dependency injection.
     * @param followRepository
     *      The follow repository to retrieve data from.
     */
    public FollowRequestsViewModel(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    /**
     * Called when the user accepts a follow request.
     * @param request
     *      The follow request to accept.
     */
    public void acceptRequest(FollowRequest request) {
        followRepository.accept(request);
    }

    /**
     * Called when the user denies a follow request.
     * @param request
     *      The follow request to deny.
     */
    public void denyRequest(FollowRequest request) {
        followRepository.deny(request);
    }

    /**
     * Get the user's follow requests.
     * @return
     *      LiveData tracking the user's follow requests.
     */
    public LiveData<List<FollowRequest>> getFollowRequests() {
        return followRepository.getFollowRequests();
    }
}
