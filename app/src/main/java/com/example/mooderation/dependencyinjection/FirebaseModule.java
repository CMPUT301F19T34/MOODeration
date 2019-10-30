package com.example.mooderation.dependencyinjection;

import com.example.mooderation.backend.Database;
import com.example.mooderation.backend.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {
    @Provides
    @Singleton
    public Database provideDatabase() {
        return new FirebaseDatabase();
    }
}
