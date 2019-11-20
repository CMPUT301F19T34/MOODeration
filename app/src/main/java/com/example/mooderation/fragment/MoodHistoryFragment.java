package com.example.mooderation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.mooderation.CustomExpandableListAdapter;
import com.example.mooderation.DeleteMoodDialog;
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
    private MoodHistoryViewModel model;

    private ArrayList<MoodEvent> moodEventList;
  
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private TreeMap<String, List<String>> expandableListDetail;
    private static FragmentManager fm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodEventList = new ArrayList<>();
        model = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);
        fm = getFragmentManager();
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

    // Create dialog when delete is selected to confirm users intention
    public static void deleteMood(int position){
        DialogFragment deleteMenu = new DeleteMoodDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        deleteMenu.setArguments(bundle);
        deleteMenu.show(fm, "DELETE");
    }
}
