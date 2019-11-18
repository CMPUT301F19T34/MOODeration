package com.example.mooderation.backend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private final FirebaseUser user;

    MutableLiveData<List<FollowRequest>> requests;

    public FollowRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // TODO implement real dependency injection
    public FollowRepository(FirebaseUser user, FirebaseFirestore firestore) {
        this.user = user;
        this.firestore = firestore;
    }

    public Task<Void> follow(Participant participant) {
        return firestore.collection("users").document(user.getUid()).get().continueWithTask(task -> {
            String username = (String) task.getResult().get("username");
            FollowRequest request = new FollowRequest(user.getUid(), username, Timestamp.now());
            return followRequestOf(participant.getUid()).document(user.getUid()).set(request);
        });
    }

    public Task<Void> accept(FollowRequest request) {
        Participant follower = new Participant(request.getUid(), request.getUsername());
        return followRequestOf(user.getUid()).document(request.getUid()).delete().continueWithTask(
                task -> followersOf(user.getUid()).document(follower.getUid()).set(follower));
    }

    public Task<Void> deny(FollowRequest request) {
        return followRequestOf(user.getUid()).document(request.getUid()).delete();
    }


    public LiveData<Boolean> isFollowing(Participant otherUser) {
        MutableLiveData<Boolean> following = new MutableLiveData<>(false);

        followersOf(otherUser.getUid()).document(user.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                following.setValue(queryDocumentSnapshots.exists());
            }
            else if (e != null) {
                // TODO handle error
            }
        }));

        return following;
    }

    public LiveData<Boolean> isRequestSent(Participant otherUser) {
        MutableLiveData<Boolean> sent = new MutableLiveData<>(false);

        followRequestOf(otherUser.getUid()).document(user.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
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

            followRequestOf(user.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
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

    private CollectionReference followersOf(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("followers");
    }

    // for sending follower requests
    private CollectionReference followRequestOf(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("follow_requests");
    }
}
