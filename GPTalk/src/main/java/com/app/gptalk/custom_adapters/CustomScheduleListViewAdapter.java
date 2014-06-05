package com.app.gptalk.custom_adapters;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.gptalk.R;
import com.app.gptalk.listview_getter.GPScheduleListItem;

public class CustomScheduleListViewAdapter extends ArrayAdapter<GPScheduleListItem> {

    private Context context;

    public CustomScheduleListViewAdapter(Context context, int resourceId, List<GPScheduleListItem> items) {

        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {

        TextView tvFirstName, tvLastName, tvBookingTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        GPScheduleListItem scheduleItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.gp_schedule_listview_item, null);

            viewHolder = new ViewHolder();
            initialiseWidgets(viewHolder, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, scheduleItem);

        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.tvFirstName = (TextView) view.findViewById(R.id.tvSchedulePatientFirstName);
        viewHolder.tvLastName = (TextView) view.findViewById(R.id.tvSchedulePatientLastName);
        viewHolder.tvBookingTime = (TextView) view.findViewById(R.id.tvSchedulePatientBookingTime);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, GPScheduleListItem scheduleListItem) {

        viewHolder.tvFirstName.setText(scheduleListItem.getFirstName());
        viewHolder.tvLastName.setText(scheduleListItem.getLastName());
        viewHolder.tvBookingTime.setText(scheduleListItem.getBookingTime());
    }
}