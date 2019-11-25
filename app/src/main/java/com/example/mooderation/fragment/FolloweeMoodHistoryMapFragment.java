package com.example.mooderation.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mooderation.R;
import com.example.mooderation.viewmodel.FolloweeMoodHistoryMapViewModel;

public class FolloweeMoodHistoryMapFragment extends Fragment {

    private FolloweeMoodHistoryMapViewModel mViewModel;

    public static FolloweeMoodHistoryMapFragment newInstance() {
        return new FolloweeMoodHistoryMapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followee_mood_history_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FolloweeMoodHistoryMapViewModel.class);
        // TODO: Use the ViewModel
    }

}
