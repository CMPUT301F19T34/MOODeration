package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Follower;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowRequestRepository;
import com.example.mooderation.backend.FollowerRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FollowRequestsViewModel extends ViewModel {
    private FollowerRepository followerRepository;
    private FollowRequestRepository followRequestRepository;
    private ListenerRegistration listenerRegistration;
    private Participant participant;

    private MutableLiveData<List<FollowRequest>> followRequests;

    public FollowRequestsViewModel() {
        followerRepository = new FollowerRepository();
        followRequestRepository = new FollowRequestRepository();
        followRequests = new MutableLiveData<>(new ArrayList<>());
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        listenerRegistration = followRequestRepository.addListener(participant, requests -> {
            Collections.sort(requests, (l, r) ->
                    -l.getCreateTimestamp().compareTo(r.getCreateTimestamp()));
            followRequests.setValue(requests);
        });
    }

    public Task<Void> acceptRequest(FollowRequest request) {
        Follower follower = new Follower(request.getUid(), request.getUsername());
        followRequestRepository.remove(participant, request);
        return followerRepository.add(participant, follower);
    }

    public Task<Void> denyRequest(FollowRequest request) {
        return followRequestRepository.remove(participant, request);
    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        return followRequests;
    }
}
