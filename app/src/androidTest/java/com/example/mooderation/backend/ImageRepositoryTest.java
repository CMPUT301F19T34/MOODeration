package com.example.mooderation.backend;

import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ImageRepositoryTest {
    @Mock
    private static FirebaseStorage storage;

    // repositories for testing
    private ImageRepository imageRepository;

    @Before
    public void setup() {
        imageRepository = new ImageRepository(FirebaseStorage.getInstance());
    }

    @Test
    public void testUpload() {
    }

    @Test
    public void testDownload() {

    }

    @Test
    public void testDelete() {

    }
}
