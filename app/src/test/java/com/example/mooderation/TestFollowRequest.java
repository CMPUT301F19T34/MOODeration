package com.example.mooderation;

import com.google.firebase.Timestamp;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnit4.class)
public class TestFollowRequest {
    private FollowRequest mockFollowRequst;
    private Timestamp timestamp;

    @Before
    public void setUp() {
        timestamp = Timestamp.now();
        mockFollowRequst = new FollowRequest("uid", "name", timestamp);
    }

    @Test
    public void testGetUid() {
        assertEquals("uid", mockFollowRequst.getUid());
    }

    @Test
    public void testGetUsername() {
        assertEquals("name", mockFollowRequst.getUsername());
    }

    @Test
    public void testGetTimestamp() {
        assertEquals(timestamp, mockFollowRequst.getCreateTimestamp());
    }

    @Test
    public void testEquals() throws InterruptedException {
        TestCase.assertEquals(mockFollowRequst, new FollowRequest("uid", "name", timestamp));
        assertNotEquals(mockFollowRequst, new FollowRequest("uid1", "name", timestamp));
        Thread.sleep(500);
        assertNotEquals(mockFollowRequst, new FollowRequest("uid", "name1", timestamp));
        assertNotEquals(mockFollowRequst, new FollowRequest("uid", "name", Timestamp.now()));
        assertNotEquals(0, mockFollowRequst);
        assertNotEquals(null, mockFollowRequst);
    }

    @Test
    public void testEmptyConstructor() {
        FollowRequest followRequest = new FollowRequest();
        assertEquals("", followRequest.getUid());
        assertEquals("", followRequest.getUsername());
    }
}
