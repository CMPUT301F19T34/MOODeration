package com.example.mooderation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mooderation.viewmodel.FollowRequestsViewModel;
import com.example.mooderation.viewmodel.ParticipantViewModel;

import java.util.ArrayList;
import java.util.List;

public class FollowRequestsFragment extends Fragment {
    private FollowRequestAdapter adapter;
    private FollowRequestsViewModel model;
    private List<FollowRequest> followRequestList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(FollowRequestsViewModel.class);

        followRequestList = new ArrayList<>();
        adapter = new FollowRequestAdapter(getContext(), followRequestList);
        model.getFollowRequests().observe(this, requests -> {
            followRequestList.clear();
            followRequestList.addAll(requests);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_requests, container, false);
        ListView listView = view.findViewById(R.id.follow_request_list);
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
        return view;
    }
}
