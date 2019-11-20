package com.example.mooderation;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.mooderation.fragment.MoodHistoryFragment;
//import com.example.mooderation.fragment.MoodHistoryFragmentDirections;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;

import java.util.List;
import java.util.TreeMap;

/**
 *Displays mood history with ability to open drop down lists displaying details
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private TreeMap<String, List<String>> expandableListDetail;
    Button editbutton;
    Button deletebutton;
    private MoodHistoryViewModel moodHistoryViewModel;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       TreeMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    /**
     *Get Child item of list header
     * @param listPosition
     *  Position in the main list which has been selected
     * @param expandedListPosition
     *  Position in the expanded list which has been selected
     * @return
     *  child item at position listed
     */
    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    /**
     * Get child item ID
     * @param listPosition
     *  Position in the main list which has been selected
     * @param expandedListPosition
     *   Position in the expanded list which has been selected
     * @return
     *   ID of child item at position listed
     */
    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    /**
     * Creates view when list is expanded to view details. Adds edit and delete button at end of details
     * @param listPosition
     * @param expandedListPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return View created for expand list details
     */
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);
        editbutton = convertView.findViewById(R.id.EditButton);
        deletebutton = convertView.findViewById(R.id.DeleteButton);

        if(isLastChild){
            editbutton.setVisibility(View.VISIBLE);
            deletebutton.setVisibility(View.VISIBLE);
            editbutton.setOnClickListener((View v) -> {
                NavDirections action = MoodHistoryFragmentDirections
                        .actionViewMoodHistoryFragmentToEditMoodEventFragment();
                Fragment editFragment = new Fragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", listPosition);
                editFragment.setArguments(bundle);
                action.getArguments();
                Navigation.findNavController(v).navigate(R.id.editMoodEventFragment, bundle);
            });
            deletebutton.setOnClickListener((View v) -> {
                MoodHistoryFragment.deleteMood(listPosition);
                //expandableListTitle.remove(listPosition);
                //this.notifyDataSetChanged();

            });
        }else{
            editbutton.setVisibility(View.GONE);
            deletebutton.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);

        // Set colors and emoticons based on mood
        if(listTitle.substring(0,5).equals("Happy")){
            String happyEmot = new String(Character.toChars(0x1F60A)); // Happy emoticon
            listTitleTextView.setText(happyEmot + " " + listTitle);
            listTitleTextView.setTextColor(-16729344); // Green
        }else if(listTitle.substring(0,3).equals("Sad")){
            String sadEmot = new String(Character.toChars(0x1F622)); // Sad emoticon
            listTitleTextView.setText(sadEmot + " " + listTitle);
            listTitleTextView.setTextColor(-16776961); // Blue
        }else if(listTitle.substring(0,3).equals("Mad")){
            String madEmot = new String(Character.toChars(0x1F620)); // Mad emoticon
            listTitleTextView.setText(madEmot + " " + listTitle);
            listTitleTextView.setTextColor(-65536); // Red
        }else{listTitleTextView.setText(listTitle);}

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

}
