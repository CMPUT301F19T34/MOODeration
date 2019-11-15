package com.example.mooderation;

import androidx.lifecycle.ViewModel;

/**
 * Stores a participant object to be shared across fragments
 */
public class ParticipantViewModel extends ViewModel {
    private Participant participant;

    /**
     * Constructor
     * Sets participant to null
     * TODO update to take participant as param
     */
    public ParticipantViewModel() {
        participant = null;
    }

    /**
     * Set the global participant instance
     * TODO safe-guard against setting the user twice
     * @param participant The global participant instance
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * Get the global participant instance
     * Throw RunTimeException if participant has not been set
     * @return The global participant instance
     */
    public Participant getParticipant() {
        if (participant == null) {
            throw new RuntimeException("Participant not set!");
        }

        return participant;
    }
}
