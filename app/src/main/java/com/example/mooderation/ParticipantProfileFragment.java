package com.example.mooderation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ParticipantProfileFragment extends Fragment {
    private ParticipantProfileViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(ParticipantProfileViewModel.class);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participant_profile, container, false);
        Bundle args = getArguments();
        Participant user = new Participant(args.getString("user_uid"), args.getString("user_username"));
        Participant other = new Participant(args.getString("other_uid"), args.getString("other_username"));

        model.setParticipants(user, other);

        View followingView = view.findViewById(R.id.is_following_you);
        followingView.setVisibility(View.INVISIBLE);
        model.getOtherFollowingThis().observe(this, isOtherFollowingThis -> {
            followingView.setVisibility(isOtherFollowingThis ? View.VISIBLE : View.INVISIBLE);
        });

        Button followButton = view.findViewById(R.id.follow_button);
        model.getThisFollowingOther().observe(this, isThisFollowingOther -> {
            Log.e("TAG", "This following other: " + isThisFollowingOther);
            followButton.setEnabled(!isThisFollowingOther);
        });

        followButton.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Send a follow request to " + model.getUsername() + "?")
                   .setMessage("If this participant accepts your follow request, you will be able to see their most recent mood event in your feed.")
                   .setPositiveButton("Send request", (d, i) -> model.sendFollowRequest())
                   .setNegativeButton("Cancel", (d, i) -> {})
                   .show();
        });

        return view;
    }
}