package com.example.mooderation.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.mooderation.CustomExpandableListAdapter;
import com.example.mooderation.DeleteMoodDialog;
import com.example.mooderation.ExpandableListDataPump;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.FollowedMoodsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FollowedMoodsFragment extends Fragment {
    private FollowedMoodsViewModel followedMoodsViewModel;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private Map<String, List<String>> expandableListDetail;
    private static FragmentManager fragmentManager;

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

            expandableListDetail = ExpandableListDataPump.getFollowed(getContext(), moodEvents);
            expandableListView = view.findViewById(R.id.followedListView);
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(
                    this.getContext(), expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);
        });
        return view;
    }
}
