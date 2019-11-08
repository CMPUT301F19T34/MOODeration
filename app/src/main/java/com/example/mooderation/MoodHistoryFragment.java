package com.example.mooderation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mooderation.viewmodel.MoodHistoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Fragment for viewing the User's own MoodEvents.
 */
public class MoodHistoryFragment extends Fragment {
    private MoodHistoryViewModel model;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MoodEvent> moodEventList;
  
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private TreeMap<String, List<String>> expandableListDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodEventList = new ArrayList<>();
        model = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);
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
        model.getMoodHistory().observe(this, moodHistory -> {
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
}
