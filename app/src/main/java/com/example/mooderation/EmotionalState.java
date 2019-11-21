package com.example.mooderation;

/**
 * Enum of the possible Emotional States for a MoodEvent
 */
public enum EmotionalState implements MoodEventConstants {
    HAPPY(R.string.mood_happy),
    SAD(R.string.mood_sad),
    MAD(R.string.mood_mad),
    FEAR(R.string.mood_fear),
    DISGUST(R.string.mood_disgust);

    // TODO define mood colors here?

    private int stringResource;

    EmotionalState(int stringResource) {
        this.stringResource = stringResource;
    }

    @Override
    public int getStringResource() {
        return stringResource;
    }
}
