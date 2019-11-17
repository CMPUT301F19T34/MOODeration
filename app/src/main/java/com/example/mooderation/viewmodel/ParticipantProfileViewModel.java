package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowRepository;
import com.example.mooderation.backend.FollowRequestRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ParticipantProfileViewModel extends ViewModel {
    private FollowRepository followRepository;
    private FollowRequestRepository followRequestRepository;
    private List<ListenerRegistration> listenerRegistrations;

    private MutableLiveData<Boolean> isFollowRequestSent;
    private MutableLiveData<String> username;
    private Participant user;
    private Participant other;

    public ParticipantProfileViewModel() {
        followRepository = new FollowRepository();
        followRequestRepository = new FollowRequestRepository();
        listenerRegistrations = new ArrayList<>();

        isFollowRequestSent = new MutableLiveData<>();
        username = new MutableLiveData<>();
    }

    public void setParticipant(Participant user) {
        this.user = user;
    }

    // TODO refactor
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
    }

    public LiveData<Boolean> getFollowRequestSent() {
        return isFollowRequestSent;
    }

    public LiveData<Boolean> getThisFollowingOther() {
        LiveData<Boolean> temp = new MutableLiveData<>(false);
        return temp; // TODO remove -- this is a temp fix
        //return followRepository.isFollowing(other);
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public Task<Void> sendFollowRequest() {
        return followRepository.follow(other);
//        FollowRequest request = new FollowRequest(user.getUid(), user.getUsername(), Timestamp.now());
//        return followRequestRepository.register(other, request);
    }
}
