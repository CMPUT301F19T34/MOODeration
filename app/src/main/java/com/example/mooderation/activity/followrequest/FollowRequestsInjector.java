package com.example.mooderation.activity.followrequest;

/**
 * A utility class which makes it easier to swap out the FollowRequestComponent with another.
 * This is used for mocking the database in instrumented tests.
 * See e.g. https://stackoverflow.com/a/43190108
 */
public class FollowRequestsInjector {
    private static FollowRequestsComponent component = DaggerFollowRequestsComponent.builder().build();

    public static FollowRequestsComponent get() {
        return component;
    }

    public static void set(FollowRequestsComponent component) {
        FollowRequestsInjector.component = component;
    }
}
