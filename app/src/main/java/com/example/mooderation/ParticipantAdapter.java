package com.example.mooderation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends ArrayAdapter<Participant> {
    private List<Participant> participantList;

    public ParticipantAdapter(@NonNull Context context) {
        this(context, new ArrayList<>());
    }
    private ParticipantAdapter(@NonNull Context context, List<Participant> participantList) {
        super(context, 0, participantList);
        this.participantList = participantList;
    }

    public void update(List<Participant> participantList) {
        this.participantList.clear();
        this.participantList.addAll(participantList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.participant_search_result, parent, false);
        }
        Participant participant = getItem(position);
        ((TextView) convertView.findViewById(R.id.username)).setText(participant.getUsername());
        return convertView;
    }
}
