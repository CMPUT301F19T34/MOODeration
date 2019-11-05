package com.example.mooderation;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnit4.class)
public class TestFollower {
    private Follower mockFollower;

    @Before
    public void setUp() {
        mockFollower = new Follower("uid", "name");
    }

    @Test
    public void testGetUid() {
        assertEquals("uid", mockFollower.getUid());
    }

    @Test
    public void testGetUsername() {
        assertEquals("name", mockFollower.getUsername());
    }

    @Test
    public void testEquals() {
        TestCase.assertEquals(mockFollower, new Follower("uid", "name"));
        assertNotEquals(mockFollower, new Follower("uid1", "name"));
        assertNotEquals(mockFollower, new Follower("uid", "name1"));
        assertNotEquals(0, mockFollower);
        assertNotEquals(null, mockFollower);
    }

    @Test
    public void testEmptyConstructor() {
        Follower follower = new Follower();
        assertEquals("", follower.getUid());
        assertEquals("", follower.getUsername());
    }
}
