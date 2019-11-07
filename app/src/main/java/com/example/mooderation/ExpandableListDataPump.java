package com.example.mooderation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.mooderation.backend.DateComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Populates expandable list of mood event history
 */
public class ExpandableListDataPump {
    /**
     * Fetches data and populates TreeMap
     * @param moodEventData
     *  LiveData List of mood objects representing mood history
     * @return
     *  Populated Tree map with mood event history
     */
    public static TreeMap<String, List<String>> getData(ArrayList<MoodEvent> moodEventData) {
        //List<MoodEvent> moodList = moodEventData.getValue();
        TreeMap<String, List<String>> expandableListDetail = new TreeMap<String, List<String>>(new DateComparator());
        for(int i = 0; i < moodEventData.size(); i++){
            MoodEvent moodEvent = moodEventData.get(i);
            List<String> temp = new ArrayList<String>();
            temp.add("Mood: " + moodEvent.getEmotionalState().toString());
            temp.add("Date: " + moodEvent.getFormattedDate());
            temp.add("Time: " + moodEvent.getFormattedTime());
            temp.add("Social situation: " + moodEvent.getSocialSituation());
            temp.add("Reason: " + moodEvent.getReason());
            expandableListDetail.put(moodEvent.getEmotionalState().toString() + "     " + moodEvent.getFormattedDate() + "     " + moodEvent.getFormattedTime(), temp);
        }
        return expandableListDetail;
    }
}