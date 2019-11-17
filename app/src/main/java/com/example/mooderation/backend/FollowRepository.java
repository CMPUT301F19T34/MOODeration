package com.example.mooderation.backend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
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
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    MutableLiveData<List<FollowRequest>> requests;

    public Task<Void> follow(Participant participant) {
        FollowRequest request = LoginRepository.getInstance().getfollowRequest();
        return followRequestRef(participant).document(user.getUid()).set(request);
    }

    public Task<Void> unfollow(Participant participant) {
        return followersRef().document(participant.getUid()).delete();
    }

    public Task<Void> acceptRequest(FollowRequest request) {
        Participant follower = new Participant(request.getUid(), request.getUsername());
        followRequestRef().document(request.getUid()).delete(); // TODO fix race condition
        return followersRef().document(follower.getUid()).set(follower);
    }

    public Task<Void> denyRequest(FollowRequest request) {
        return followRequestRef().document(request.getUid()).delete();
    }

//    public LiveData<Boolean> isFollowing(Participant participant) {
//        MutableLiveData<Boolean> following = new MutableLiveData<>();
//
//        followingRef().document(participant.getUid()).addSnapshotListener((queryDocumentSnapshots, e) -> {
//            if (queryDocumentSnapshots != null) {
//                following.setValue(queryDocumentSnapshots.exists());
//            }
//            else if (e != null) {
//                // TODO handle error
//            }
//        });
//
//        return following;
//    }

    public LiveData<List<FollowRequest>> getFollowRequests() {
        if (requests != null) {
            return requests;
        }

        requests = new MutableLiveData<>();
        followRequestRef().addSnapshotListener(((queryDocumentSnapshots, e) -> {
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

        return requests;
    }

    private CollectionReference followersRef() {
        return firestore.collection("users")
                .document(user.getUid())
                .collection("followers");
    }

    private CollectionReference followRequestRef() {
        return firestore.collection("users")
                .document(user.getUid())
                .collection("follow_requests");
    }

    // for sending follower requests
    private CollectionReference followRequestRef(Participant participant) {
        return firestore.collection("users")
                .document(participant.getUid())
                .collection("follow_requests");
    }
}
