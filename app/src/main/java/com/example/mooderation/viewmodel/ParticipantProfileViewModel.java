package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowRepository;
import com.google.android.gms.tasks.Task;

public class ParticipantProfileViewModel extends ViewModel {
    private FollowRepository followRepository;

    private Participant viewing;
    private MutableLiveData<String> username = new MutableLiveData<>();

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

        username.setValue(viewing.getUsername());
        requestSent = Transformations.map(followRepository.isRequestSent(viewing), sent -> sent);
        following = Transformations.map(followRepository.isFollowing(viewing), following -> following);
    }

    public LiveData<Boolean> getFollowRequestSent() {
        return requestSent;
    }

    public LiveData<Boolean> getThisFollowingOther() {
        return following;
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public Task<Void> sendFollowRequest() {
        return followRepository.follow(viewing);
    }
}
