package com.example.mooderation;

// https://stackoverflow.com/questions/5469629
public enum EmotionalState {
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
