package com.example.mooderation.backend;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.google.firebase.Timestamp;

// TODO probably could be named better
public class LoginRepository {
    private static volatile LoginRepository instance = null;

    // private constructor for singleton
    private LoginRepository() {}

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    private Participant participant;

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant(){
        return participant;
    }

    // TODO temporary until follow request is refactored
    // gets a follow request from the currently logged in user
    public FollowRequest getfollowRequest() {
        return new FollowRequest(participant.getUid(), participant.getUsername(), Timestamp.now());
    }
}
