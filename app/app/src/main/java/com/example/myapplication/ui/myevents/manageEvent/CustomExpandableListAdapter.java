package com.example.myapplication.ui.myevents.manageEvent;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.objects.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Author: Erin-Marie
 * Expandable List Adapter, used for the Manage Events Fragment
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private HashMap<String, ArrayList<UserProfile>> expandableListDetail;
    private Context context;
    private List<String> expandableListTitle;

    public CustomExpandableListAdapter(Context context,  ArrayList<String> expandableListTitle,
                                       HashMap<String, ArrayList<UserProfile>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }
    @Override
    public UserProfile getChild(int listPosition, int expandedListPosition) {
        return (UserProfile) Objects.requireNonNull(this.expandableListDetail.get(this.expandableListTitle.get(listPosition)))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    /**
     * Author: Erin-Marie
     * Method that displays the list item within the group view within the expanded list view
     * @param listPosition the position of the list group in the expanded list
     * @param expandedListPosition the position of the list item in the expanded list group
     * @param convertView view
     * @param parent viewGroup parent
     * @return convertView which is the view for the list group, and has the correct title attr
     */
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        UserProfile userItem = getChild(listPosition, expandedListPosition);
        assert userItem != null;
        String userName = userItem.getName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.org_manage_list_item, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        if (userName == null){
            //if a user does not have a Name set in their profile, they are displayed as Anonymous Noodle
            expandedListTextView.setText("Anonymous_Noodle");
        } else {
            expandedListTextView.setText(userItem.getName());
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

    /**
     * Author: Erin-Marie
     * Method that displays the group view within the expanded list view
     * @param listPosition the position of the list group in the expanded list
     * @param isExpanded boolean
     * @param convertView view
     * @param parent viewGroup parent
     * @return convertView which is the view for the list group, and has the correct title attr
     */
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.org_manage_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
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

    /**
     * Author Erin-Marie
     * Method to delete a list item from the list view for ManageEvent
     *
     * @param listPosition         list group selected
     * @param expandedListPosition list item selected
     * @return user UserProfile that was just removed from the list
     */
    public UserProfile deleteChild(int listPosition, int expandedListPosition){
        UserProfile user = getChild(listPosition, expandedListPosition);
        //remove the user from the visible list
        (Objects.requireNonNull(this.expandableListDetail.get(this.expandableListTitle.get(listPosition)))).remove(expandedListPosition);
        this.notifyDataSetChanged();
        return user;
    }
}

