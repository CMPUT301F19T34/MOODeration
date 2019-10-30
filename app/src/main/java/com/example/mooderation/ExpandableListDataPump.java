package com.example.mooderation;

import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    //private MoodHistoryViewModel moodHistory;

    //private MoodHistoryViewModel moodHistory = ViewModelProviders.of(MainActivity.this).get(MoodHistoryViewModel.class);
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> Happy = new ArrayList<String>();
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
        expandableListDetail.put("Sad 2019-10-28 10:00 AM", Sad);
        return expandableListDetail;
    }
}