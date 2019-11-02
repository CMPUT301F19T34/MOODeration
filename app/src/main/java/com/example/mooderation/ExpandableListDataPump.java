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
            temp.add("Time: " + moodEvent.getTime());
            expandableListDetail.put(moodEvent.getEmotionalState().toString() + "     " + moodEvent.getDate() + "     " + moodEvent.getTime(), temp);
        }

        /*List<String> Happy = new ArrayList<String>();
        Happy.add("Mood: Happy");
        Happy.add("Date: 2019-10-28");
        Happy.add("Time: 10:50 AM");
        Happy.add("Situation: None");
        Happy.add("Reason: None");

        List<String> Sad = new ArrayList<String>();
        Sad.add("Mood: Sad");
        Sad.add("Date: 2019-10-28");
        Sad.add("Time: 10:00 AM");
        Sad.add("Situation: None");
        Sad.add("Reason: None");



        expandableListDetail.put("Happy 2019-10-28 10:45 AM", Happy);
        expandableListDetail.put("Sad 2019-10-28 10:00 AM", Sad);*/
        return expandableListDetail;
    }
}