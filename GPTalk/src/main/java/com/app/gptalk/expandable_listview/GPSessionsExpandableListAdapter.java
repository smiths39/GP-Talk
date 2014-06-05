package com.app.gptalk.expandable_listview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.app.gptalk.R;

import java.util.HashMap;
import java.util.List;

public class GPSessionsExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> lvListHeader;
    private HashMap<String, List<String>> hmListChild;
    private String categoryTitle;
    private TextView tvConsultationType, tvConsultationCategory, tvConsultationDate;
    private int color;

    public GPSessionsExpandableListAdapter(Context context, List<String> lvListHeader, HashMap<String, List<String>> hmListChild, String categoryTitle, int color) {

        this.context = context;
        this.lvListHeader = lvListHeader;
        this.hmListChild = hmListChild;
        this.categoryTitle = categoryTitle;
        this.color = color;
    }

    @Override
    public int getGroupCount() {

        return this.lvListHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this.hmListChild.get(this.lvListHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return this.lvListHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this.hmListChild.get(this.lvListHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.consultation_listview_group, null);
        }

        tvConsultationType = (TextView) convertView.findViewById(R.id.tvConsultationType);
        tvConsultationCategory = (TextView) convertView.findViewById(R.id.tvConsultationCategory);

        // Specify title, category and color of each expandable listview
        tvConsultationType.setText(headerTitle);
        tvConsultationCategory.setText(categoryTitle);
        tvConsultationCategory.setTextColor(color);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {

            // Initialise layout of expandable listview content
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.consultation_listview_item, null);
        }

        tvConsultationDate = (TextView) convertView.findViewById(R.id.tvConsultationDate);
        tvConsultationDate.setText("    " + childText);
        tvConsultationDate.setTextColor(color);

        return convertView;
    }

    // If a date is selected, a list of consultations relating to selected date is displayed
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}
