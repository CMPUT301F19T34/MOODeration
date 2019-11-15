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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ParticipantProfileViewModel extends ViewModel {
    private FollowerRepository followerRepository;
    private FollowRequestRepository followRequestRepository;
    private List<ListenerRegistration> listenerRegistrations;

    private MutableLiveData<Boolean> isThisFollowingOther;
    private MutableLiveData<Boolean> isFollowRequestSent;
    private MutableLiveData<String> username;
    private Participant user;
    private Participant other;

    public ParticipantProfileViewModel() {
        followerRepository = new FollowerRepository();
        followRequestRepository = new FollowRequestRepository();
        listenerRegistrations = new ArrayList<>();

        isThisFollowingOther = new MutableLiveData<>();
        isFollowRequestSent = new MutableLiveData<>();
        username = new MutableLiveData<>();
    }

    public void setParticipant(Participant user) {
        this.user = user;
    }

    public void setViewingParticipant(Participant other) {
        this.other = other;

        for (ListenerRegistration reg : listenerRegistrations) {
            reg.remove();
        }
        listenerRegistrations.clear();

        username.setValue(other.getUsername());
        listenerRegistrations.add(followRequestRepository.addListener(other, requests -> {
                    // TODO: don't just load everything and search it -- search the database
                    for (FollowRequest r : requests) {
                        if (r.getUid().equals(user.getUid())) {
                            isFollowRequestSent.setValue(true);
                            return;
                        }
                    }
                    isFollowRequestSent.setValue(false);
                }));
        listenerRegistrations.add(followerRepository.addListener(other, followers ->
                isThisFollowingOther.setValue(followers.contains(Follower.fromParticipant(user)))));
    }

    public LiveData<Boolean> getFollowRequestSent() {
        return isFollowRequestSent;
    }

    public LiveData<Boolean> getThisFollowingOther() {
        return isThisFollowingOther;
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public Task<Void> sendFollowRequest() {
        FollowRequest request = new FollowRequest(user.getUid(), user.getUsername(), Timestamp.now());
        return followRequestRepository.add(other, request);
    }
}
