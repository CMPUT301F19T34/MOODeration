package com.example.mooderation;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
    public static TreeMap<String, List<String>> getData(Context context, ArrayList<MoodEvent> moodEventData) {
        TreeMap<String, List<String>> expandableListDetail = new TreeMap<String, List<String>>(new DateComparator());
        for(int i = 0; i < moodEventData.size(); i++){
            MoodEvent moodEvent = moodEventData.get(i);
            List<String> temp = new ArrayList<String>();
            temp.add("Mood: " + moodEvent.getEmotionalState().toString());
            temp.add("Date: " + moodEvent.getFormattedDate());
            temp.add("Time: " + moodEvent.getFormattedTime());
            temp.add("Social situation: " + moodEvent.getSocialSituation());
            temp.add("Reason: " + moodEvent.getReason());
            expandableListDetail.put(
                    context.getString(moodEvent.getEmotionalState().getStringResource())
                            + "     " + moodEvent.getFormattedDate()
                            + "     " + moodEvent.getFormattedTime(), temp);
        }
        return expandableListDetail;
    }
}

class DateComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2){
        String[] st1 = s1.split("     ");
        String[] st2 = s2.split("     ");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aa");
        Date date1 = new Date();
        Date date2 = new Date();
        try {
            date1 = formatter.parse(st1[1]+ " " + st1[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            date2 = formatter.parse(st2[1]+ " " + st2[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date2.compareTo(date1);
    }
}