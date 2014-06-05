package com.app.gptalk.custom_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gptalk.R;
import com.app.gptalk.listview_getter.GPHistoryItem;

import java.util.List;

public class CustomHistoryListViewAdapter extends ArrayAdapter<GPHistoryItem> {

    private Context context;

    public CustomHistoryListViewAdapter(Context context, int resourceId, List<GPHistoryItem> items) {

        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {

        ImageView ivProfilePic;
        TextView tvFirstName, tvLastName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        GPHistoryItem historyItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.gp_history_listview_item, null);

            viewHolder = new ViewHolder();
            initialiseWidgets(viewHolder, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, historyItem);

        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.ivProfilePic = (ImageView) view.findViewById(R.id.ivGPHistoryListPatientProfilePic);
        viewHolder.tvFirstName = (TextView) view.findViewById(R.id.tvGPHistoryPatientFirstName);
        viewHolder.tvLastName = (TextView) view.findViewById(R.id.tvGPHistoryPatientLastName);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, GPHistoryItem item) {

        viewHolder.ivProfilePic.setImageBitmap(item.getProfilePhoto());
        viewHolder.tvFirstName.setText(item.getFirstName());
        viewHolder.tvLastName.setText(item.getLastName());
    }
}
