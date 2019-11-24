package com.example.mooderation.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.MoodHistoryMapViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MoodHistoryMapFragment extends Fragment {

    private MoodHistoryMapViewModel moodHistoryMapViewModel;
    private GoogleMap googleMap;
    private Set<Marker> markers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers = new HashSet<>();
        moodHistoryMapViewModel = ViewModelProviders.of(this).get(MoodHistoryMapViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood_history_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(map -> googleMap = map);
        moodHistoryMapViewModel.getMoodHistoryWithLocation().observe(this, moodHistory -> {
            googleMap.clear();
            Random random = new Random();
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            for (MoodEvent moodEvent : moodHistory) {
                String title = getResources().getString(moodEvent.getEmotionalState().getStringResource());
                String snippet = moodEvent.getReason();
                // LatLng position = new LatLng(moodEvent.getLocation().getLatitude(), moodEvent.getLocation().getLongitude());
                LatLng position = new LatLng(53.631611 + random.nextDouble()*0.1, -113.323975 + random.nextDouble()*0.1);
                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(moodEvent.getEmotionalState().getHue());
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(title)
                        .snippet(snippet)
                        .position(position)
                        .icon(icon));
                bounds.include(position);
            }

            googleMap.setMinZoomPreference(8);
            googleMap.setMaxZoomPreference(20);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 15));
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setLatLngBoundsForCameraTarget(bounds.build());
        });
    }
}
