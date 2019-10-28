package com.example.mooderation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Stores information about a user's mood event
 */
public class MoodEvent {
    public static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    public static final DateFormat timeFormat = SimpleDateFormat.getTimeInstance();

    private Calendar dateTime;
    private EmotionalState emotionalState;
    private String reason;
    private SocialSituation socialSituation;

    /**
     * MoodEvent Constructor
     * @param dateTime
     *      A Calendar with the date and time of the MoodEvent.
     * @param emotionalState
     *      The emotional state for this MoodEvent
     * @param socialSituation
     *      The social situation for this MoodEvent
     * @param reason
     *      The reason for this MoodEvent
     */
    public MoodEvent(Calendar dateTime, EmotionalState emotionalState,
                     SocialSituation socialSituation, String reason) {
        this.dateTime = dateTime;
        this.emotionalState = emotionalState;
        this.socialSituation = socialSituation;
        this.reason = reason;
    }

    public String getDate() {
        return dateFormat.format(dateTime.getTime());
    }

    public String getTime() {
        return timeFormat.format(dateTime.getTime());
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public String getReason() {
        return reason;
    }

    public SocialSituation getSocialSituation() {
        return socialSituation;
    }
}
