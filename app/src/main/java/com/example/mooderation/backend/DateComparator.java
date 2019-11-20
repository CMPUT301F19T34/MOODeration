package com.example.mooderation.backend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Simple comparator to sort by descending date value
 */
public class DateComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2){
        String[] st1 = s1.split("     ");
        String[] st2 = s2.split("     ");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
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
