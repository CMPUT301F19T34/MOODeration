package com.example.mooderation.backend;

import com.example.mooderation.Participant;

// TODO merge with participant repository?
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
}
