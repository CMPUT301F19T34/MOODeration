package com.example.mooderation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FollowRequestAdapter extends ArrayAdapter<FollowRequest> {
    public FollowRequestAdapter(@NonNull Context context, @NonNull List<FollowRequest> requests) {
        super(context, 0, requests);
    }

    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.follow_request, parent, false);
        }
        FollowRequest request = getItem(position);
        @NonNull TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        title.setText("Follow request");
        description.setText(request.getParticipant().getUsername() + " wants to follow you");
        return convertView;
    }
}
