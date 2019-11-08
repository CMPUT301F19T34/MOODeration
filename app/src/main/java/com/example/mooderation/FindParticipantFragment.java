package com.example.mooderation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class FindParticipantFragment extends Fragment {

    private FindParticipantViewModel model;
    private ParticipantAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(this).get(FindParticipantViewModel.class);
        adapter = new ParticipantAdapter(getContext());
        model.getSearchResults().observe(this, participants -> adapter.update(participants));
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

        String uid = getArguments().getString("uid");
        String username = getArguments().getString("username");
        model.setParticipant(new Participant(uid, username));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Participant p = adapter.getItem(i);

                NavDirections action = FindParticipantFragmentDirections
                        .actionFindParticipantFragmentToParticipantProfileFragment(
                                model.getCurrentParticipant().getUid(),
                                model.getCurrentParticipant().getUsername(),
                                p.getUid(),
                                p.getUsername()
                        );
                Navigation.findNavController(view).navigate(action);
            }
        });
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
                model.filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                model.filter(s);
                return true;
            }
        });
    }
}
