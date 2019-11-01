package com.example.mooderation;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Represents a single request by a given participant to follow the current user
 */
public class FollowRequest {
    public FollowRequest(String uid, String username, Timestamp createTimestamp) {
        this.uid = uid;
        this.username = username;
        this.createTimestamp = createTimestamp;
    }

    public FollowRequest() {
        uid = "";
        username = "";
        createTimestamp = Timestamp.now();
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Gets the creation time of the follow request
     *
     * @return The creation time of the follow request
     */
    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    private String uid;
    private String username;
    private Timestamp createTimestamp;
}
