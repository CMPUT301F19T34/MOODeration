package com.example.mooderation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    private MutableLiveData<Boolean> isOtherFollowingThis;
    private MutableLiveData<String> username;
    private Participant user;
    private Participant other;

    public ParticipantProfileViewModel() {
        followerRepository = new FollowerRepository();
        followRequestRepository = new FollowRequestRepository();
        listenerRegistrations = new ArrayList<>();

        isThisFollowingOther = new MutableLiveData<>();
        isOtherFollowingThis = new MutableLiveData<>();
        username = new MutableLiveData<>();
    }

    public void setParticipants(Participant user, Participant other) {
        this.user = user;
        this.other = other;

        for (ListenerRegistration reg : listenerRegistrations) {
            reg.remove();
        }
        listenerRegistrations.clear();

        username.setValue(other.getUsername());
        listenerRegistrations.add(followerRepository.addListener(user, followers -> {
            isOtherFollowingThis.setValue(followers.contains(Follower.fromParticipant(other)));
        }));
        listenerRegistrations.add(followerRepository.addListener(other, followers -> {
            isThisFollowingOther.setValue(followers.contains(Follower.fromParticipant(user)));
        }));
    }

    public LiveData<Boolean> getThisFollowingOther() {
        return isThisFollowingOther;
    }

    public LiveData<Boolean> getOtherFollowingThis() {
        return isOtherFollowingThis;
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public Task<Void> sendFollowRequest() {
        FollowRequest request = new FollowRequest(user.getUid(), user.getUsername(), Timestamp.now());
        return followRequestRepository.add(other, request);
    }
}
