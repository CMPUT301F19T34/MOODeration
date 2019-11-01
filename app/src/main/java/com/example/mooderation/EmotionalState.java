package com.example.mooderation;

/**
 * Enum of the possible Emotional States for a MoodEvent
 * Used to populate spinners in AddMoodEventFragment
 * See: https://stackoverflow.com/questions/5469629
 */
public enum EmotionalState {
    /**
     * TODO
     * Should be updated to used Android string resources in the future
     * There is probably a better way to implement this and it should be looked
     * at again in the future.
     */
    HAPPY("Happy"),
    SAD("Sad"),
    MAD("Mad");

    public String externalName;

    EmotionalState(String externalName) {
        this.externalName = externalName;
    }

    @Override
    public String toString() {
        return externalName;
    }
}
