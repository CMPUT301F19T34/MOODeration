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
    private SocialSituation socialSituation;
    private String reason;
    private MoodLatLng location;

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
     * @param location
     *      The location of this MoodEvent
     */
    public MoodEvent(Date date, EmotionalState emotionalState,
                     SocialSituation socialSituation, String reason, MoodLatLng location) {
        this.date = date;
        this.emotionalState = emotionalState;
        this.socialSituation = socialSituation;
        this.reason = reason;
        this.location = location;
    }

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
        this.location = null;
    }

    /**
     * An empty constructor to allow this to be serialized by Firebase
     */
    public MoodEvent() {
        this.date = new Date();
        this.emotionalState = EmotionalState.HAPPY;
        this.socialSituation = SocialSituation.NONE;
        this.reason = "";
    }

    // TODO this will not work if date or time can be changed
    public String getId() {
        return String.valueOf(getDate().getTime());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public void setEmotionalState(EmotionalState emotionalState) {
        this.emotionalState = emotionalState;
    }

    public SocialSituation getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(SocialSituation socialSituation) {
        this.socialSituation = socialSituation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public MoodLatLng getLocation() {
        return location;
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
        if (location != null && !moodEvent.location.equals(location))
            return false;
        return true;
    }
}
