package com.example.mooderation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MoodEventAdapter extends RecyclerView.Adapter<MoodEventAdapter.ViewHolder> {
    private ArrayList<MoodEvent> moodEventData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView moodTextView;
        public TextView dateTextView;
        public TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moodTextView = itemView.findViewById(R.id.mood_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
        }
    }

    public MoodEventAdapter(ArrayList<MoodEvent> data) {
        moodEventData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mood_event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoodEvent moodEvent = moodEventData.get(position);
        holder.moodTextView.setText(moodEvent.getEmotionalState().toString());
        holder.dateTextView.setText(moodEvent.getDate());
        holder.timeTextView.setText(moodEvent.getTime());
    }

    @Override
    public int getItemCount() {
        return moodEventData.size();
    }
}
