package com.example.mooderation;

import com.google.firebase.Timestamp;

/**
 * Represents a single request by a given participant to follow the current user
 */
public class FollowRequest {
    /**
     * Initializes the FollowRequest
     *
     * @param participant      Participant who is requesting to follow the current user
     * @param createTimestamp  Creation time of the follow request
     */
    public FollowRequest(Participant participant, Timestamp createTimestamp) {
        this.participant = participant;
        this.createTimestamp = createTimestamp;
    }

    /**
     * Gets the participant who is requesting to follow the current user
     *
     * @return The participant who is requesting to follow the current user
     */
    public Participant getParticipant() {
        return participant;
    }

    /**
     * Gets the creation time of the follow request
     *
     * @return The creation time of the follow request
     */
    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    private Participant participant;
    private Timestamp createTimestamp;
}
