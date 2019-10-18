package com.example.mooderation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Stores information about a user's mood event
 */
public class MoodEvent {
    public enum EmotionalState {
        HAPPY,
        SAD,
        MAD
    }

    public enum SocialSituation {
        NONE,
    }

    private Calendar dateTime;
    private EmotionalState emotionalState;
    private String reason = null;
    private SocialSituation socialSituation = SocialSituation.NONE;

    /**
     * MoodEvent Constructor
     * @param dateTime
     *      A Calendar with the date and time of the MoodEvent.
     * @param emotionalState
     *      The emotional state for this MoodEvent
     */
    public MoodEvent(Calendar dateTime, EmotionalState emotionalState) {
        this.dateTime = dateTime;
        this.emotionalState = emotionalState;
    }

    /**
     * Set a new reason for the MoodEvent
     * @param reason
     *      The new reason for the MoodEvent
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Set the social situation of the MoodEvent
     * @param socialSituation
     *      The new social situation
     */
    public void setSocialSituation(SocialSituation socialSituation) {
        this.socialSituation = socialSituation;
    }
}
