package com.app.gptalk.custom_adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.gptalk.listview_getter.GPAvailableTimeItem;
import com.app.gptalk.listview_getter.GPWebsiteItem;
import com.app.gptalk.R;

import java.util.List;

public class CustomAvailableTimeListViewAdapter extends ArrayAdapter<GPAvailableTimeItem> {

    private Context context;

    public CustomAvailableTimeListViewAdapter(Context context, int resource, List<GPAvailableTimeItem> items) {

        super(context, resource, items);
        this.context = context;
    }

    private class ViewHolder {

        TextView tvTime, tvStatus;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        GPAvailableTimeItem gpAvailableTimeItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.gp_available_time_listview_item, null);
            viewHolder = new ViewHolder();

            initialiseWidgets(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, gpAvailableTimeItem);
        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.tvTime = (TextView) view.findViewById(R.id.tvGPAvailableTimeListTime);
        viewHolder.tvStatus = (TextView) view.findViewById(R.id.tvGPAvailableTimeListStatus);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, GPAvailableTimeItem gpAvailableTimeItem) {

        viewHolder.tvTime.setText(gpAvailableTimeItem.getTime());
        viewHolder.tvStatus.setText(gpAvailableTimeItem.getStatus());

        if (gpAvailableTimeItem.getStatus().equals("Unavailable")) {
            viewHolder.tvStatus.setTextColor(Color.RED);
        } else if (gpAvailableTimeItem.getStatus().equals("Pending")) {
            viewHolder.tvStatus.setTextColor(Color.YELLOW);
        } else {
            viewHolder.tvStatus.setTextColor(Color.GREEN);
        }
    }
}
