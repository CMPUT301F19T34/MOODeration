package com.example.mooderation.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.MoodHistoryMapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * The fragment for the "My Mood History Map" view
 */
public class MoodHistoryMapFragment extends Fragment {

    private MoodHistoryMapViewModel moodHistoryMapViewModel;
    private GoogleMap googleMap;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        googleMap = null;
        mapFragment.getMapAsync(map -> {
            googleMap = map;
            updateMarkers(moodHistoryMapViewModel.getMoodHistoryWithLocation().getValue());
        });
        moodHistoryMapViewModel.getMoodHistoryWithLocation().observe(this, this::updateMarkers);
    }

    private float getHue(EmotionalState emotionalState) {
        float[] hsv = new float[]{0.f, 0.f, 0.f};
        Color.colorToHSV(ResourcesCompat.getColor(getResources(), emotionalState.getMarkerColor(), null), hsv);
        return hsv[0];
    }

    private void updateMarkers(@Nullable List<MoodEvent> moodHistory) {
        if (googleMap == null) {
            return;
        }
        googleMap.clear();
        googleMap.setMinZoomPreference(8);
        googleMap.setMaxZoomPreference(20);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (moodHistory == null || moodHistory.isEmpty()) {
            return;
        }
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (MoodEvent moodEvent : moodHistory) {
            String title = getResources().getString(moodEvent.getEmotionalState().getStringResource());
            String snippet = moodEvent.getFormattedDate() + "\n" + moodEvent.getReason();
            LatLng position = new LatLng(moodEvent.getLocation().getLatitude(), moodEvent.getLocation().getLongitude());
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(getHue(moodEvent.getEmotionalState()));
            googleMap.addMarker(new MarkerOptions()
                    .title(title)
                    .snippet(snippet)
                    .position(position)
                    .icon(icon));
            bounds.include(position);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 15));
        googleMap.setLatLngBoundsForCameraTarget(bounds.build());
    }
}
