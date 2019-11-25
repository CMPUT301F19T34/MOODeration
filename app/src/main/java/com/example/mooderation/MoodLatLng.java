package com.example.mooderation;

import com.google.android.gms.maps.model.LatLng;

public class MoodLatLng {
    private double latitude;
    private double longitude;

    public MoodLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public MoodLatLng() {
        this(0.0, 0.0);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
