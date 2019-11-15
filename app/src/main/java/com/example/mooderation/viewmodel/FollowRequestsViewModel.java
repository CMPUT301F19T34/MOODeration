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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FollowRequestsViewModel extends ViewModel {
    private FollowerRepository followerRepository = new FollowerRepository();
    private FollowRequestRepository followRequestRepository = new FollowRequestRepository();

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // TODO remove
    private Participant dummyParticipant;

    private ListenerRegistration listenerRegistration;
    private MutableLiveData<List<FollowRequest>> followRequests =
            new MutableLiveData<>(new ArrayList<>());

    public FollowRequestsViewModel() {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            updateListener();
        });
    }

    private void updateListener() {
        if (listenerRegistration != null) listenerRegistration.remove();
        if (user == null) return;

        dummyParticipant = new Participant(user.getUid(), "dummy"); // TODO remove

        listenerRegistration = followRequestRepository.addListener(dummyParticipant, requests -> {
            Collections.sort(requests, (l, r) ->
                    -l.getCreateTimestamp().compareTo(r.getCreateTimestamp()));
            followRequests.setValue(requests);
        });
    }

    public Task<Void> acceptRequest(FollowRequest request) {
        Follower follower = new Follower(request.getUid(), request.getUsername());
        followRequestRepository.remove(dummyParticipant, request);
        return followerRepository.add(dummyParticipant, follower);
    }

    public Task<Void> denyRequest(FollowRequest request) {
        return followRequestRepository.remove(dummyParticipant, request);
    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        updateListener();
        return followRequests;
    }

    public void forceUpdate() {
        followRequests.setValue(followRequests.getValue());
    }
}
