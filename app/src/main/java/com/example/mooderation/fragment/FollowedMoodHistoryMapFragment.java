package com.example.mooderation.fragment;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.FollowedMoodHistoryMapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class FollowedMoodHistoryMapFragment extends Fragment {

    private FollowedMoodHistoryMapViewModel followedMoodHistoryMapViewModel;
    private GoogleMap googleMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followedMoodHistoryMapViewModel = ViewModelProviders.of(getActivity()).get(FollowedMoodHistoryMapViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followed_mood_history_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        googleMap = null;
        mapFragment.getMapAsync(map -> {
            googleMap = map;
            updateMarkers(followedMoodHistoryMapViewModel.getFollowedMoodEventsWithLocation().getValue());
        });
        followedMoodHistoryMapViewModel.getFollowedMoodEventsWithLocation().observe(this, this::updateMarkers);
    }

    private void updateMarkers(@Nullable HashMap<Participant, MoodEvent> moodHistory) {
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
        for (HashMap.Entry<Participant, MoodEvent> entry : moodHistory.entrySet()) {
            Participant participant = entry.getKey();
            MoodEvent moodEvent = entry.getValue();

            String title = getResources().getString(moodEvent.getEmotionalState().getStringResource());
            String snippet = participant.getUsername() + "\n" + moodEvent.getFormattedDate() + "\n" + moodEvent.getReason();
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

    private float getHue(EmotionalState emotionalState) {
        float[] hsv = new float[]{0.f, 0.f, 0.f};
        Color.colorToHSV(ResourcesCompat.getColor(getResources(), emotionalState.getMarkerColor(), null), hsv);
        return hsv[0];
    }
}
