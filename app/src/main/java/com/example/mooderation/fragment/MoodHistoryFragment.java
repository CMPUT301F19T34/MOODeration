package com.example.mooderation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.mooderation.viewmodel.MoodEventViewModel;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fragment for viewing the User's own MoodEvents.
 */
public class MoodHistoryFragment extends Fragment {
    private MoodHistoryViewModel moodHistoryViewModel;
    private MoodEventViewModel moodEventViewModel;

    private ArrayList<MoodEvent> moodEventList = new ArrayList<>();
  
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private Map<String, List<String>> expandableListDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodHistoryViewModel = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);
        moodEventViewModel = ViewModelProviders.of(getActivity()).get(MoodEventViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history_layout,
                container, false);

        final FloatingActionButton addMoodEventButton = view.findViewById(R.id.add_mood_event_button);
        addMoodEventButton.setOnClickListener((View v) -> {
            moodEventViewModel.setMoodEvent(new MoodEvent());
            NavDirections action = MoodHistoryFragmentDirections.actionViewMoodHistoryFragmentToAddMoodEventFragment();
            Navigation.findNavController(v).navigate(action);
        });

        expandableListView = view.findViewById(R.id.expandableListView);

        //When new mood is added refresh expandable list
        moodHistoryViewModel.getMoodHistory().observe(this, moodHistory -> {
            moodEventList.clear();
            moodEventList.addAll(moodHistory);
            // Populate expandable list with new data
            expandableListDetail = ExpandableListDataPump.getData(getContext(), moodEventList);
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(
                    this.getContext(), expandableListTitle, expandableListDetail,
                    // on edit button pressed listener
                    position -> {
                        moodEventViewModel.setMoodEvent(moodEventList.get(position));
                        Navigation.findNavController(view).navigate(R.id.addMoodEventFragment);
                    },
                    // on delete button pressed listener
                    position -> {
                        // TODO
                    });

            // Specify adapter for expandable list
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

            case R.id.fear_filter:
                moodHistoryViewModel.setFilter(EmotionalState.FEAR);
                break;

            case R.id.disgust_filter:
                moodHistoryViewModel.setFilter(EmotionalState.DISGUST);
                break;

            default:
        }
        return true;
    }
}
