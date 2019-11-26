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
 * Database abstraction for accessing a participants followers and follow requests.
 */
public class FollowRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseUser user;

    private MutableLiveData<List<Participant>> followers;
    private MutableLiveData<List<FollowRequest>> requests;

    /**
     * Default constructor for the FollowRepository.
     * Generates dependencies internally.
     */
    public FollowRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Constructor for FollowRepository used for testing.
     * Dependencies are passed to constructor.
     * @param user
     *      A FirebaseUser that the repository will track.
     * @param firestore
     *      A instance of FirebaseFirestore.
     */
    public FollowRepository(FirebaseUser user, FirebaseFirestore firestore) {
        this.user = user;
        this.firestore = firestore;
    }

    /**
     * Sends a follow request to another participant.
     * @param participant
     *      The participant to follow.
     * @return
     *      Database operation Task.
     */
    public Task<Void> follow(Participant participant) {
        return firestore.collection("users").document(user.getUid()).get().continueWithTask(task -> {
            String username = (String) task.getResult().get("username");
            FollowRequest request = new FollowRequest(user.getUid(), username, Timestamp.now());
            return followRequestOf(participant.getUid()).document(user.getUid()).set(request);
        });
    }

    /**
     * Accepts a follow request from another user.
     * @param request
     *      The follow request to accept.
     * @return
     *      Database operation Task.
     */
    public Task<Void> accept(FollowRequest request) {
        Participant follower = new Participant(request.getUid(), request.getUsername());
        return followRequestOf(user.getUid()).document(follower.getUid()).delete().continueWithTask(
                task -> followersOf(user.getUid()).document(follower.getUid()).set(follower)).continueWithTask(
                task -> following(follower.getUid()).document(user.getUid()).set(user));
    }

    /**
     * Denies a follow request from another user.
     * @param request
     *      The follow request to deny.
     * @return
     *      Database operation Task.
     */
    public Task<Void> deny(FollowRequest request) {
        return followRequestOf(user.getUid()).document(request.getUid()).delete();
    }

    /**
     * Check if the current user is following another participant.
     * @param other
     *      The other participant.
     * @return
     *      LiveData tracking the boolean condition.
     */
    public LiveData<Boolean> isFollowing(Participant other) {
        MutableLiveData<Boolean> following = new MutableLiveData<>(false);

        followersOf(other.getUid()).document(user.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                following.setValue(queryDocumentSnapshots.exists());
            }
            else if (e != null) {
                // TODO handle error
            }
        }));

        return following;
    }

    /**
     * Check if the current user has already sent a follow request to another participant.
     * @param other
     *      The other participant.
     * @return
     *      LiveData tracking the boolean condition.
     */
    public LiveData<Boolean> isRequestSent(Participant other) {
        MutableLiveData<Boolean> sent = new MutableLiveData<>(false);

        followRequestOf(other.getUid()).document(user.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                sent.setValue(queryDocumentSnapshots.exists());
            }
            else if (e != null) {
                // TODO handle error
            }
        }));

        return sent;
    }

    /**
     * Get a list of the current user's follow request.
     * @return
     *      LiveData tracking the user's follow requests.
     */
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

    /**
     * Get the current user's current followers.
     * @return
     *      LiveData tracking the user's follower list.
     */
    public LiveData<List<Participant>> getFollowers() {
        if (followers == null) {
            followers = new MutableLiveData<>();

            followersOf(user.getUid()).addSnapshotListener(((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    List<Participant> followerList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        followerList.add(doc.toObject(Participant.class));
                    }
                    followers.setValue(followerList);
                }
                else if (e != null) {
                    // TODO
                }
            }));
        }

        return followers;
    }

    /**
     * Get a reference to a user's followers.
     * @param userId
     *      The user's ID.
     * @return
     *      A reference to the firestore collection for followers.
     */
    private CollectionReference followersOf(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("followers");
    }

    /**
     * Get a reference to the users that a user is following
     * @param userId
     *      The user's ID.
     * @return
     *      A reference to the firestore collection for followers.
     */
    private CollectionReference following(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("following");
    }

    /**
     * Get a reference to as user's follow requests.
     * @param userId
     *      The user's ID.
     * @return
     *      A reference to the firestore collection for follow requests.
     */
    private CollectionReference followRequestOf(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("follow_requests");
    }
}
