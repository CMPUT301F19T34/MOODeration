package com.example.mooderation.backend;

import com.example.mooderation.Participant;
import com.google.firebase.auth.FirebaseUser;
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
    @Mock
    FirebaseUser user;
    @Mock
    FirebaseFirestore firestore;
    private FollowRepository followRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        followRepository = new FollowRepository(user, firestore);
    }

    @Test
    public void testFollow() {
        when(user.getUid()).thenReturn("mock-uid");
        // TODO going to need a better way to mock firebase


        Participant followMe = new Participant("follow-uid", "follow-me");
        followRepository.follow(followMe);
    }
}
