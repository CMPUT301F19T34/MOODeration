package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowRepository;

public class ParticipantProfileViewModel extends ViewModel {
    private FollowRepository followRepository;

    private Participant viewing;
    private MutableLiveData<String> username;

    private LiveData<Boolean> requestSent;
    private LiveData<Boolean> following;

    public ParticipantProfileViewModel() {
        this.followRepository = new FollowRepository();
    }

    // TODO implement real dependency injection
    public ParticipantProfileViewModel(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void setViewingParticipant(Participant viewing) {
        this.viewing = viewing;

        if (username == null) {
            username = new MutableLiveData<>();
        }
        username.setValue(viewing.getUsername());

        requestSent = Transformations.map(followRepository.isRequestSent(viewing), sent -> sent);
        following = Transformations.map(followRepository.isFollowing(viewing), following -> following);
    }

    public LiveData<Boolean> getFollowRequestSent() {
        if (requestSent == null) {
            throw new IllegalStateException("setViewingParticipant must be called first");
        }
        return requestSent;
    }

    public LiveData<Boolean> getThisFollowingOther() {
        if (following == null) {
            throw new IllegalStateException("setViewingParticipant must be called first");
        }
        return following;
    }

    public LiveData<String> getUsername() {
        if (username == null) {
            throw new IllegalStateException("setViewingParticipant must be called first");
        }
        return username;
    }

    public void sendFollowRequest() {
        if (viewing == null) {
            throw new IllegalStateException("setViewingParticipant must be called first");
        }
        followRepository.follow(viewing);
    }
}
