package com.example.mooderation;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     *  LiveData List of mood objects representing mood history
     * @return
     *  Populated Tree map with mood event history
     */
    public static Map<String, List<String>> getFollowed(Context context, HashMap<String, MoodEvent> moodEventData) {
        Map<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        for (String uid : moodEventData.keySet()) {
            //Log.d("FOLLOWED_MOODS",
            //String.format("%s: %s", uid, moodEventData.get(uid).getEmotionalState()));
            String thiss = String.format("%s: %s", uid, moodEventData.get(uid).getEmotionalState());
            MoodEvent moodEvent = moodEventData.get(uid);
            List<String> temp = new ArrayList<>();
            temp.add("Mood: " + context.getString(moodEvent.getEmotionalState().getStringResource()));
            temp.add("Date: " + moodEvent.getFormattedDate());
            temp.add("Time: " + moodEvent.getFormattedTime());
            temp.add("Social situation: " + context.getString(moodEvent.getSocialSituation().getStringResource()));
            temp.add("Reason: " + moodEvent.getReason());
            expandableListDetail.put(
                    uid + '\n' +
                    context.getString(moodEvent.getEmotionalState().getStringResource())
                            + "     " + moodEvent.getFormattedDate()
                            + "     " + moodEvent.getFormattedTime(), temp);

        }
        return expandableListDetail;
    }
}