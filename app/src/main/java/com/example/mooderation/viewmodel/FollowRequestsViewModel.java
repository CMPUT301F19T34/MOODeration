package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.backend.FollowRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class FollowRequestsViewModel extends ViewModel {
    private FollowRepository followRepository = new FollowRepository();

    public Task<Void> acceptRequest(FollowRequest request) {
        return followRepository.acceptRequest(request);
//        Follower follower = new Follower(request.getUid(), request.getUsername());
//        followRequestRepository.remove(dummyParticipant, request);
//        return followRepository.add(follower);
    }

    public Task<Void> denyRequest(FollowRequest request) {
        return followRepository.denyRequest(request);
        //return followRequestRepository.remove(dummyParticipant, request);
    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        return followRepository.getFollowRequests();
    }
}
