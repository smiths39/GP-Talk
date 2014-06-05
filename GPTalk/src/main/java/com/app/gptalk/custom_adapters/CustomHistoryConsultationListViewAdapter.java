package com.app.gptalk.custom_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.gptalk.R;
import com.app.gptalk.listview_getter.GPHistoryListItem;

import java.util.List;

public class CustomHistoryConsultationListViewAdapter extends ArrayAdapter<GPHistoryListItem> {

    private Context context;

    public CustomHistoryConsultationListViewAdapter(Context context, int resourceId, List<GPHistoryListItem> items) {

        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {

        TextView tvBookingDate, tvBookingTime, tvBookingStatus;
    }

    // Populate retrieved data in listview layout design
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        GPHistoryListItem historyConsultationItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.gp_history_consultation_listview_item, null);

            viewHolder = new ViewHolder();
            initialiseWidgets(viewHolder, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetsResources(viewHolder, historyConsultationItem);

        return convertView;
    }

    // Getter methods are used to populate textviews within listview
    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.tvBookingDate = (TextView) view.findViewById(R.id.tvGPHistoryConsultationBookingDate);
        viewHolder.tvBookingTime = (TextView) view.findViewById(R.id.tvGPHistoryConsultationBookingTime);
        viewHolder.tvBookingStatus = (TextView) view.findViewById(R.id.tvGPHistoryConsultationBookingStatus);
    }

    private void setWidgetsResources(ViewHolder viewHolder, GPHistoryListItem item) {

        viewHolder.tvBookingDate.setText(item.getBookingDate());
        viewHolder.tvBookingTime.setText(item.getBookingTime());
        viewHolder.tvBookingStatus.setText(item.getBookingStatus());
    }
}
