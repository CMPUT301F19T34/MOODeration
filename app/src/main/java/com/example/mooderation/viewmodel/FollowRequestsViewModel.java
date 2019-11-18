package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.backend.FollowRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FollowRequestsViewModel extends ViewModel {
    private FollowRepository followRepository = new FollowRepository(FirebaseFirestore.getInstance()); // TODO temp

    public Task<Void> acceptRequest(FollowRequest request) {
        return followRepository.accept(request);
    }

    public Task<Void> denyRequest(FollowRequest request) {
        return followRepository.deny(request);
    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        return followRepository.getFollowRequests();
    }
}
