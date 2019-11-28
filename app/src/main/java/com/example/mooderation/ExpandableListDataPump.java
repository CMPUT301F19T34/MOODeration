package com.example.mooderation;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Populates expandable list of mood event history
 */
public class ExpandableListDataPump {
    /**
     * Fetches data and populates HashMap for mood history
     * @param moodEventData
     *  LiveData List of mood objects representing mood history
     * @return
     *  Populated Hash map with mood event history
     */
    public static Map<String, List<String>> getData(Context context, ArrayList<MoodEvent> moodEventData) {
        Map<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        for(int i = 0; i < moodEventData.size(); i++){
            MoodEvent moodEvent = moodEventData.get(i);
            List<String> temp = new ArrayList<String>();
            temp.add("Mood: " + context.getString(moodEvent.getEmotionalState().getStringResource()));
            temp.add("Date: " + moodEvent.getFormattedDate());
            temp.add("Time: " + moodEvent.getFormattedTime());
            temp.add("Social situation: " + context.getString(moodEvent.getSocialSituation().getStringResource()));
            temp.add("Reason: " + moodEvent.getReason());
            expandableListDetail.put(
                    context.getString(moodEvent.getEmotionalState().getStringResource())
                            + "     " + moodEvent.getFormattedDate()
                            + "     " + moodEvent.getFormattedTime(), temp);
        }
        return expandableListDetail;
    }

    /**
     * Fetches data and populates TreeMap for followed moods
     * @param moodEventData
     *  LiveData List of mood objects representing moods of followed friends
     * @return
     *  Populated Tree map with followed friends most recent moods
     */
    public static Map<String, List<String>> getFollowed(Context context, HashMap<Participant, MoodEvent> moodEventData) {
        Map<String, List<String>> expandableListDetail = new TreeMap<String, List<String>>(dateCompare);
        for (Participant uid : moodEventData.keySet()) {
            MoodEvent moodEvent = moodEventData.get(uid);
            List<String> temp = new ArrayList<>();
            temp.add("User: "+ uid.getUsername());
            temp.add("Mood: " + context.getString(moodEvent.getEmotionalState().getStringResource()));
            temp.add("Date: " + moodEvent.getFormattedDate());
            temp.add("Time: " + moodEvent.getFormattedTime());
            temp.add("Social situation: " + context.getString(moodEvent.getSocialSituation().getStringResource()));
            temp.add("Reason: " + moodEvent.getReason());
            expandableListDetail.put(
                    uid.getUsername() + '\n' +
                    context.getString(moodEvent.getEmotionalState().getStringResource())
                            + "     " + moodEvent.getFormattedDate()
                            + "     " + moodEvent.getFormattedTime(), temp);
        }
        return expandableListDetail;
    }

    private static Comparator<String> dateCompare = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2){
            String[] st1 = s1.split("     ");
            String[] st2 = s2.split("     ");
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = formatter.parse(st1[1]+ " " + st1[2]);
                date2 = formatter.parse(st2[1]+ " " + st2[2]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date2.compareTo(date1);
        }
    };
}