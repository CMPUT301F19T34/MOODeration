package com.example.mooderation;

// https://stackoverflow.com/questions/5469629
public enum SocialSituation {
    NONE("Not Specified");

    public String externalName;

    SocialSituation(String externalName) {
        this.externalName = externalName;
    }

    @Override
    public String toString() {
        return externalName;
    }
}
