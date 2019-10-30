package com.example.mooderation.activity.followrequest;

import com.example.mooderation.dependencyinjection.FirebaseModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FirebaseModule.class})
public interface FollowRequestsComponent {
    void inject(FollowRequestsActivity activity);
}
