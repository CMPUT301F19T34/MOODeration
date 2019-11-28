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


    /**
     * Returns the hue of the mood event constant
     * @return
     *     The hue of the mood event constant, as a floating-point value from in (0, 360)
     */
    public int getMarkerColor() {
        switch (stringResource) {
            case R.string.mood_happy:
                return R.color.happy_marker;
            case R.string.mood_sad:
                return R.color.sad_marker;
            case R.string.mood_mad:
                return R.color.mad_marker;
            case R.string.mood_disgust:
                return R.color.disgusted_marker;
            case R.string.mood_fear:
                return R.color.afraid_marker;
        }
        return 0;
    }
}
