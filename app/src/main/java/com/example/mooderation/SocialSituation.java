package com.example.mooderation;

/**
 * Enum of the possible Social Situations for a MoodEvent
 */
public enum SocialSituation implements MoodEventConstants {
    NONE(R.string.social_none),
    ALONE(R.string.social_alone),
    ONE_PERSON(R.string.social_one_person),
    SEVERAL_PEOPLE(R.string.social_several_people),
    CROWD(R.string.social_crowd);

    private int stringResource;

    SocialSituation(int stringResource) {
        this.stringResource = stringResource;
    }

    @Override
    public int getStringResource() {
        return stringResource;
    }
}
