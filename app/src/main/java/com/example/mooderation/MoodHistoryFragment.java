package com.example.mooderation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing the User's own MoodEvents.
 */
public class MoodHistoryFragment extends Fragment {
    private MoodHistoryViewModel model;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MoodEvent> moodEventList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);

        moodEventList = new ArrayList<>();
        adapter = new MoodEventAdapter(moodEventList);
        model.getMoodHistory().observe(this, moodHistory -> {
            moodEventList.clear();
            moodEventList.addAll(moodHistory);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history_layout,
                container, false);
        recyclerView = view.findViewById(R.id.mood_event_recycler_view);

        // specify layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify adapter for recycler view
        recyclerView.setAdapter(adapter);

        final FloatingActionButton addMoodEventButton = view.findViewById(R.id.add_mood_event_button);
        addMoodEventButton.setOnClickListener((View v) -> {
            NavDirections action = MoodHistoryFragmentDirections.
                    actionViewMoodHistoryFragmentToAddMoodEventFragment();
            Navigation.findNavController(v).navigate(action);
        });

        return view;
    }
}
