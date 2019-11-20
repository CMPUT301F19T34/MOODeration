package com.example.mooderation;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 *Displays mood history with ability to open drop down lists displaying details
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private Map<String, List<String>> expandableListDetail;

    public interface ButtonPressListener {
        public void onButtonPress(int position);
    }

    private ButtonPressListener editListener;
    private ButtonPressListener deleteListener;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       Map<String, List<String>> expandableListDetail,
                                       ButtonPressListener editListener,
                                       ButtonPressListener deleteListener) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
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

        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);

        Button editbutton = convertView.findViewById(R.id.EditButton);
        Button deletebutton = convertView.findViewById(R.id.DeleteButton);

        if(isLastChild){
            editbutton.setVisibility(View.VISIBLE);
            deletebutton.setVisibility(View.VISIBLE);

            editbutton.setOnClickListener((View v) -> editListener.onButtonPress(listPosition));
            deletebutton.setOnClickListener((View v) -> deleteListener.onButtonPress(listPosition));
        }
        else {
            editbutton.setVisibility(View.GONE);
            deletebutton.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
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

        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        // Set colors and emoticons based on mood
        if(listTitle.contains("Happy")) {
            listTitleTextView.setTextColor(-16729344); // Green
        }
        else if (listTitle.contains("Sad")) {
            listTitleTextView.setTextColor(-16776961); // Blue
        }
        else if(listTitle.contains("Mad")) {
            listTitleTextView.setTextColor(-65536); // Red
        }

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

