package com.example.mooderation;

/**
 * Enum of the possible Social Situations for a MoodEvent
 * Used to populate spinners in MoodEventFragment
 * See: https://stackoverflow.com/questions/5469629
 */
public enum SocialSituation {
    /**
     * TODO
     * Should be updated to used Android string resources in the future
     * There is probably a better way to implement this and it should be looked
     * at again in the future.
     */
    NONE("Not Specified"),
    ALONE("Alone"),
    ONE_PERSON("One other person"),
    SEVERAL_PEOPLE("Several people"),
    CROWD("Crowd");
    // TODO use string resources here

    public String externalName;

    SocialSituation(String externalName) {
        this.externalName = externalName;
    }

    @Override
    public String toString() {
        return externalName;
    }
}
