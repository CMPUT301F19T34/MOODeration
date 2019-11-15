package com.example.mooderation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mooderation.viewmodel.FollowRequestsViewModel;

public class FollowRequestsFragment extends Fragment {
    private FollowRequestAdapter adapter;
    private FollowRequestsViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(FollowRequestsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.follow_request_list);

        adapter = new FollowRequestAdapter(getContext());
        model.getFollowRequests().observe(this, requests -> {
            adapter.update(requests);
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, itemView, i, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            FollowRequest request = adapter.getItem(i);
            if (request == null) {
                return;
            }
            builder.setTitle("Accept " + request.getUsername() + "'s follow request?")
                    .setMessage("The user " + request.getUsername() + " wants to follow your mood history. If you accept, they will be able to see the most recent mood event of your mood history in their feed.")
                    .setPositiveButton("Accept", (dialogInterface, i1) -> model.acceptRequest(request))
                    .setNegativeButton("Deny", (dialogInterface, i12) -> model.denyRequest(request))
                    .show();
        });
    }
}
