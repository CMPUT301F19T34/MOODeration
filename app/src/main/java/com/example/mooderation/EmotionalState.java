package com.example.mooderation;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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
     * Returns the hexadecimal colour of the mood event constant
     * @return
     *      The colour corresponding to the constant, as a hexadecimal integer
     */
    public int getHexColour() {
        switch (stringResource) {
            case R.string.mood_happy:
                return Color.rgb(0x37, 0xff, 0x8b);
            case R.string.mood_sad:
                return Color.rgb(0x1e, 0x3f, 0x5e);
            case R.string.mood_mad:
                return Color.rgb(0xc6, 0x3f, 0x3f);
            case R.string.mood_fear:
                return Color.rgb(0, 0, 0);
            case R.string.mood_disgust:
                return Color.rgb(0x52, 0x2b, 0x29);
        }
        return Color.rgb(0, 0, 0);
    }

    /**
     * Returns the hue of the mood event constant
     * @return
     *     The hue of the mood event constant, as a floating-point value from in (0, 360)
     */
    public float getHue() {
        switch (stringResource) {
            case R.string.mood_happy:
                return BitmapDescriptorFactory.HUE_YELLOW;
            case R.string.mood_sad:
                return BitmapDescriptorFactory.HUE_BLUE;
            case R.string.mood_mad:
                return BitmapDescriptorFactory.HUE_RED;
            case R.string.mood_fear:
                return BitmapDescriptorFactory.HUE_AZURE;
            case R.string.mood_disgust:
                return BitmapDescriptorFactory.HUE_GREEN;
        }
        return 0.f;
    }
}
