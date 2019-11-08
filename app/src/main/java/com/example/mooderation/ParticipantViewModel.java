package com.example.mooderation;

import androidx.lifecycle.ViewModel;

public class ParticipantViewModel extends ViewModel {
    private Participant participant;

    public ParticipantViewModel() {
        participant = null;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        if (participant == null) {
            throw new RuntimeException("Participant not set");
        }

        return participant;
    }
}
