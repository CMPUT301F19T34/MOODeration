package com.example.mooderation.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mooderation.Participant;
import com.example.mooderation.R;
import com.example.mooderation.viewmodel.ParticipantProfileViewModel;

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
        Participant other = new Participant(args.getString("uid"), args.getString("username"));
        model.setViewingParticipant(other);

        model.getUsername().observe(this, username -> {
            ((TextView) view.findViewById(R.id.username)).setText(username);
        });

        View requestSentView = view.findViewById(R.id.request_sent);
        requestSentView.setVisibility(View.INVISIBLE);
        model.getFollowRequestSent().observe(this, isFollowRequestSent -> {
            requestSentView.setVisibility(isFollowRequestSent ? View.VISIBLE : View.INVISIBLE);
        });

        Button followButton = view.findViewById(R.id.follow_button);
        model.getThisFollowingOther().observe(this, isThisFollowingOther -> {
            followButton.setEnabled(!isThisFollowingOther);
        });

        followButton.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Send a follow request to " + model.getUsername().getValue() + "?")
                   .setMessage(R.string.send_follow_request_menu_message)
                   .setPositiveButton(R.string.send_follow_request_menu_send, (d, i) -> model.sendFollowRequest())
                   .setNegativeButton(R.string.send_follow_request_menu_cancel, (d, i) -> {})
                   .show();
        });

        return view;
    }
}