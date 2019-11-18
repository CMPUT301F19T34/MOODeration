package com.example.mooderation.backend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the followers of each participant.
 */
public class FollowRepository {
    private final FirebaseFirestore firestore;
    private final Participant currentUser;

    MutableLiveData<List<FollowRequest>> requests;

    public FollowRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = LoginRepository.getInstance().getParticipant();
    }

    // TODO implement real dependency injection
    public FollowRepository(Participant participant, FirebaseFirestore firestore) {
        this.currentUser = participant;
        this.firestore = firestore;
    }

    public Task<Void> follow(Participant participant) {
        FollowRequest request = new FollowRequest(currentUser.getUid(), currentUser.getUsername(), Timestamp.now());
        return followRequestOf(participant).document(currentUser.getUid()).set(request);
    }

    public Task<Void> accept(FollowRequest request) {
        Participant follower = new Participant(request.getUid(), request.getUsername());
        // TODO fix race condition
        followRequestOf(currentUser).document(request.getUid()).delete();
        return followersOf(currentUser).document(follower.getUid()).set(follower);
    }

    public Task<Void> deny(FollowRequest request) {
        return followRequestOf(currentUser).document(request.getUid()).delete();
    }


    public LiveData<Boolean> isFollowing(Participant participant) {
        MutableLiveData<Boolean> following = new MutableLiveData<>(false);

        followersOf(participant).document(currentUser.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                following.setValue(queryDocumentSnapshots.exists());
            }
            else if (e != null) {
                // TODO handle error
            }
        }));

        return following;
    }

    public LiveData<Boolean> isRequestSent(Participant participant) {
        MutableLiveData<Boolean> sent = new MutableLiveData<>(false);

        followRequestOf(participant).document(currentUser.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                sent.setValue(queryDocumentSnapshots.exists());
            }
            else if (e != null) {
                // TODO handle error
            }
        }));

        return sent;
    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        if (requests == null) {
            requests = new MutableLiveData<>();

            followRequestOf(currentUser).addSnapshotListener(((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    List<FollowRequest> requestList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        requestList.add(doc.toObject(FollowRequest.class));
                    }
                    requests.setValue(requestList);
                }
                else if (e != null) {
                    // TODO handle error
                }
            }));
        }

        return requests;
    }

    private CollectionReference followersOf(Participant participant) {
        return firestore.collection("users")
                .document(participant.getUid())
                .collection("followers");
    }

    // for sending follower requests
    private CollectionReference followRequestOf(Participant participant) {
        return firestore.collection("users")
                .document(participant.getUid())
                .collection("follow_requests");
    }
}
