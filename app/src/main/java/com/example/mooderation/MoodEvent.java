package com.example.mooderation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Stores information about a user's mood event
 */
public class MoodEvent {
    public static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    public static final DateFormat timeFormat = SimpleDateFormat.getTimeInstance();

    private Date date;
    private EmotionalState emotionalState;
    private String reason;
    private SocialSituation socialSituation;

    /**
     * MoodEvent Constructor
     * @param date
     *      A Date with the date and time of the MoodEvent.
     * @param emotionalState
     *      The emotional state for this MoodEvent
     * @param socialSituation
     *      The social situation for this MoodEvent
     * @param reason
     *      The reason for this MoodEvent
     */
    public MoodEvent(Date date, EmotionalState emotionalState,
                     SocialSituation socialSituation, String reason) {
        this.date = date;
        this.emotionalState = emotionalState;
        this.socialSituation = socialSituation;
        this.reason = reason;
    }

    public MoodEvent(String moodEventId, Date date, EmotionalState emotionalState,
                     SocialSituation socialSituation, String reason) {
        this.date = date;
        this.emotionalState = emotionalState;
        this.socialSituation = socialSituation;
        this.reason = reason;
    }

    /**
     * An empty constructor to allow this to be serialized by Firebase
     */
    public MoodEvent() {}

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        return dateFormat.format(date.getTime());
    }

    public String getFormattedTime() {
        return timeFormat.format(date.getTime());
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

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        MoodEvent moodEvent = (MoodEvent) other;
        if (!moodEvent.date.equals(date))
            return false;
        if (!moodEvent.emotionalState.equals(emotionalState))
            return false;
        if (!moodEvent.socialSituation.equals(socialSituation))
            return false;
        if (!moodEvent.reason.equals(reason))
            return false;
        return true;
    }
}
