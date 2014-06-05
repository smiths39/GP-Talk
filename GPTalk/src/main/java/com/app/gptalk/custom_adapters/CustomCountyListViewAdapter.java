package com.app.gptalk.custom_adapters;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.gptalk.listview_getter.GPCountyItem;
import com.app.gptalk.R;

public class CustomCountyListViewAdapter extends ArrayAdapter<GPCountyItem> {

    private Context context;

    public CustomCountyListViewAdapter(Context context, int resourceId, List<GPCountyItem> items) {

        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {

        TextView tvCounty;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        GPCountyItem gpCountyItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.gp_listview_county_item, null);

            viewHolder = new ViewHolder();
            initialiseWidgets(viewHolder, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, gpCountyItem);

        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.tvCounty = (TextView) view.findViewById(R.id.tvGPListCounty);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, GPCountyItem gpCountyItem) {

        viewHolder.tvCounty.setText(gpCountyItem.getCounty());
    }
}