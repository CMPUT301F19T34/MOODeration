package com.example.mooderation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> Happy = new ArrayList<String>();
        Happy.add("Mood: Happy");
        Happy.add("Date: 2019-10-28");
        Happy.add("Time: 10:50 AM");
        Happy.add("Situation: None");
        Happy.add("Reason: None");

        List<String> Sad = new ArrayList<String>();
        Sad.add("Situation: None");
        Sad.add("Reason: None");



        expandableListDetail.put("Happy 2019-10-28 10:45 AM", Happy);
        expandableListDetail.put("Sad 2019-10-28 10:00 AM", Sad);
        return expandableListDetail;
    }
}