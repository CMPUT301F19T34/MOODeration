package com.example.mooderation.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.FollowedMoodsViewModel;

public class FollowedMoodsFragment extends Fragment {
    private FollowedMoodsViewModel followedMoodsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followedMoodsViewModel = ViewModelProviders.of(getActivity()).get(FollowedMoodsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followed_moods, container, false);

        followedMoodsViewModel.getMoodEvents().observe(this, moodEvents -> {
            for (Participant p : moodEvents.keySet()) {
                Log.d("FOLLOWED_MOODS",
                        String.format("%s: %s", p.getUsername(), moodEvents.get(p).getEmotionalState()));
            }
            Log.d(this.getClass().getSimpleName(), FollowedMoodsFragment.this.toString());
        });

        return view;
    }
}
