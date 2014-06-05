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
import com.app.gptalk.listview_getter.PatientSessionListItem;

public class CustomSessionListViewAdapter extends ArrayAdapter<PatientSessionListItem> {

    private Context context;

    public CustomSessionListViewAdapter(Context context, int resourceId, List<PatientSessionListItem> items) {

        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {

        TextView tvTitle, tvFirstName, tvLastName, tvBookingTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        PatientSessionListItem sessionItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.patient_session_listview_item, null);

            viewHolder = new ViewHolder();
            initialiseWidgets(viewHolder, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, sessionItem);

        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.tvTitle = (TextView) view.findViewById(R.id.tvSessionPatientTitle);
        viewHolder.tvFirstName = (TextView) view.findViewById(R.id.tvSessionPatientFirstName);
        viewHolder.tvLastName = (TextView) view.findViewById(R.id.tvSessionPatientLastName);
        viewHolder.tvBookingTime = (TextView) view.findViewById(R.id.tvSessionPatientBookingTime);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, PatientSessionListItem sessionListItem) {

        viewHolder.tvTitle.setText(sessionListItem.getTitle());
        viewHolder.tvFirstName.setText(sessionListItem.getFirstName());
        viewHolder.tvLastName.setText(sessionListItem.getLastName());
        viewHolder.tvBookingTime.setText(sessionListItem.getBookingTime());
    }
}