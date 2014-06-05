package com.app.gptalk.custom_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.gptalk.listview_getter.GPWebsiteItem;
import com.app.gptalk.R;

import java.util.List;

public class CustomWebsiteListViewAdapter extends ArrayAdapter<GPWebsiteItem> {

    private Context context;

    public CustomWebsiteListViewAdapter(Context context, int resource, List<GPWebsiteItem> items) {

        super(context, resource, items);
        this.context = context;
    }

    private class ViewHolder {

        TextView tvCounty, tvTitle, tvFirstName, tvLastName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        GPWebsiteItem gpWebsiteItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.gp_website_listview_item, null);
            viewHolder = new ViewHolder();

            initialiseWidgets(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, gpWebsiteItem);
        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.tvCounty = (TextView) view.findViewById(R.id.tvGPWebsiteCounty);
        viewHolder.tvTitle = (TextView) view.findViewById(R.id.tvGPWebsiteTitle);
        viewHolder.tvFirstName = (TextView) view.findViewById(R.id.tvGPWebsiteFirstName);
        viewHolder.tvLastName = (TextView) view.findViewById(R.id.tvGPWebsiteLastName);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, GPWebsiteItem gpWebsiteItem) {

        viewHolder.tvCounty.setText(gpWebsiteItem.getCounty());
        viewHolder.tvTitle.setText(gpWebsiteItem.getTitle());
        viewHolder.tvFirstName.setText(gpWebsiteItem.getFirstName());
        viewHolder.tvLastName.setText(gpWebsiteItem.getLastName());
    }
}
