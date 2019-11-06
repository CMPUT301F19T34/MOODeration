package com.example.mooderation;

import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ExpandableListDataPump {
    //private MoodHistoryViewModel moodHistory;

    //private MoodHistoryViewModel moodHistory = ViewModelProviders.of(MainActivity.this).get(MoodHistoryViewModel.class);
    public static TreeMap<String, List<String>> getData(ArrayList<MoodEvent> moodEventData) {
        TreeMap<String, List<String>> expandableListDetail = new TreeMap<String, List<String>>();
        for(int i = 0; i < moodEventData.size(); i++){
            MoodEvent moodEvent = moodEventData.get(i);
            List<String> temp = new ArrayList<String>();
            temp.add("Mood: " + moodEvent.getEmotionalState().toString());
            temp.add("Date: " + moodEvent.getDate());
            temp.add("Time: " + moodEvent.getFormattedTime());
            expandableListDetail.put(moodEvent.getEmotionalState().toString() + "     " + moodEvent.getDate() + "     " + moodEvent.getFormattedTime(), temp);
        }
        return expandableListDetail;
    }
}