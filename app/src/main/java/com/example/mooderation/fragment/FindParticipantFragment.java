package com.example.mooderation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.mooderation.Participant;
import com.example.mooderation.ParticipantAdapter;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.FindParticipantViewModel;

public class FindParticipantFragment extends Fragment {

    private FindParticipantViewModel findParticipantViewModel;
    private ParticipantAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ParticipantAdapter(getContext());

        findParticipantViewModel = ViewModelProviders.of(this).get(FindParticipantViewModel.class);
        findParticipantViewModel.getSearchResults().observe(this,
                participants -> adapter.update(participants));

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_participant, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.search_results);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Participant p = adapter.getItem(i);
            NavDirections action = FindParticipantFragmentDirections
                    .actionFindParticipantFragmentToParticipantProfileFragment(
                            p.getUid(),
                            p.getUsername()
                    );
            Navigation.findNavController(view1).navigate(action);
        });
        findParticipantViewModel.searchFor("");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Find a participant");
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                findParticipantViewModel.searchFor(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                findParticipantViewModel.searchFor(s);
                return true;
            }
        });
    }
}
