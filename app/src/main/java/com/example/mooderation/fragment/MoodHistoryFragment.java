package com.example.mooderation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.mooderation.CustomExpandableListAdapter;
import com.example.mooderation.EmotionalState;
import com.example.mooderation.ExpandableListDataPump;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Fragment for viewing the User's own MoodEvents.
 */
public class MoodHistoryFragment extends Fragment {
    private MoodHistoryViewModel moodHistoryViewModel;

    private ArrayList<MoodEvent> moodEventList = new ArrayList<>();
  
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private TreeMap<String, List<String>> expandableListDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodHistoryViewModel = ViewModelProviders.of(this).get(MoodHistoryViewModel.class);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history_layout,
                container, false);

        final FloatingActionButton addMoodEventButton = view.findViewById(R.id.add_mood_event_button);
        addMoodEventButton.setOnClickListener((View v) -> {
            NavDirections action = MoodHistoryFragmentDirections.
                    actionViewMoodHistoryFragmentToAddMoodEventFragment();
            Navigation.findNavController(v).navigate(action);
        });

        // Populate expandable list
        expandableListDetail = ExpandableListDataPump.getData(moodEventList);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this.getContext(), expandableListTitle, expandableListDetail);

        //When new mood is added refresh expandable list
        moodHistoryViewModel.getMoodHistory().observe(this, moodHistory -> {
            moodEventList.clear();
            moodEventList.addAll(moodHistory);
            ((BaseExpandableListAdapter)expandableListAdapter).notifyDataSetChanged();
            // Populate expandable list with new data
            expandableListDetail = ExpandableListDataPump.getData(moodEventList);
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(this.getContext(), expandableListTitle, expandableListDetail);

            // Specify adapter for expandable list
            expandableListView = view.findViewById(R.id.expandableListView);
            expandableListView.setAdapter(expandableListAdapter);
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.mood_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.no_filter:
                moodHistoryViewModel.setFilter(null);
                break;

            case R.id.happy_filter:
                moodHistoryViewModel.setFilter(EmotionalState.HAPPY);
                break;

            case R.id.sad_filter:
                moodHistoryViewModel.setFilter(EmotionalState.SAD);
                break;

            case R.id.mad_filter:
                moodHistoryViewModel.setFilter(EmotionalState.MAD);
                break;

            // TODO other mood types

            default:
        }

        return true;
    }
}
