package com.example.mooderation.backend;

import com.example.mooderation.Participant;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FollowRepositoryTest {
    @Mock Participant participant;
    @Mock FirebaseFirestore firestore;
    private FollowRepository followRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        followRepository = new FollowRepository(participant, firestore);
    }

    @Test
    public void testFollow() {
        when(participant.getUid()).thenReturn("mock-uid");
        // TODO going to need a better way to mock firebase
        // possible solution -- https://stackoverflow.com/questions/43225804/
        // unit testing in general -- https://medium.com/mindorks/unit-testing-for-viewmodel-19f4d76b20d4

        Participant followMe = new Participant("follow-uid", "follow-me");
        followRepository.follow(followMe);
    }
}
