package com.example.mooderation;

/**
 * A custom location object which is serializable by Firebase. Location is stored as latitude
 * and longitude.
 */
public class MoodLatLng {
    private double latitude;
    private double longitude;

    /**
     * Initialize the location with given coordinates
     * @param latitude Latitude of the location, in degrees
     * @param longitude Longitude of the location, in degrees
     */
    public MoodLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Initialize the location with latitude 0 and longitude 0 (used for firebase serialization)
     */
    public MoodLatLng() {
        this(0.0, 0.0);
    }

    /**
     * Gets location's latitude
     * @return The latitude of the location, in degrees
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets location's longitude
     * @return The longitude of the location, in degrees
     */
    public double getLongitude() {
        return longitude;
    }

}
