package com.example.mooderation.backend;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Repository for uploading and downloading images from Firebase Storage
 */
public class ImageRepository {
    private static final int ONE_MEGABYTE = 1024 * 1024;
    private final FirebaseStorage storage;

    /**
     * Default constructor. Creates dependencies internally
     */
    public ImageRepository() {
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Constructor for testing.
     * @param storage
     *      An instance of FirebaseStorage
     */
    public ImageRepository(FirebaseStorage storage) {
        this.storage = storage;
    }

    /**
     * Upload an image to Firebase Storage
     * @param imageUri
     *      The image's URI
     * @return
     *      An upload task for the image being uploaded.
     */
    public UploadTask uploadImage(Uri imageUri) {
        StorageReference imageRef = storage.getReference(getImagePath(imageUri));
        return imageRef.putFile(imageUri);
    }

    /**
     * Download an image from Firebase Storage
     * @param imagePath
     *      The path to the image in Firebase Storage.
     * @return
     *      The download task for the image.
     */
    public Task<byte[]> downloadImage(String imagePath) {
        StorageReference imageRef = storage.getReference(imagePath);
        return imageRef.getBytes(ONE_MEGABYTE);
    }

    /**
     * Gets the path to the file in Firebase Storage
     * @param imageUri
     *      The image's URI
     * @return
     *      The path to the image in Firebase Storage
     */
    public String getImagePath(Uri imageUri) {
        return "images/" + imageUri.getLastPathSegment();
    }
}
