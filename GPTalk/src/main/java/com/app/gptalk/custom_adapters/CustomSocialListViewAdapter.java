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
import com.app.gptalk.listview_getter.SupportItem;

import java.util.List;

public class CustomSocialListViewAdapter extends ArrayAdapter<SupportItem> {

    private Context context;

    public CustomSocialListViewAdapter(Context context, int resourceId, List<SupportItem> items) {

        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {

        ImageView websiteLogo;
        TextView websiteTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        SupportItem supportItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.social_website_listview_item, null);

            viewHolder = new ViewHolder();
            initialiseWidgets(viewHolder, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, supportItem);


        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.websiteTitle = (TextView) view.findViewById(R.id.tvSocialWebsiteTitle);
        viewHolder.websiteLogo = (ImageView) view.findViewById(R.id.ivSocialWebsiteLogo);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, SupportItem item) {

        viewHolder.websiteTitle.setText(item.getWebsite());
        viewHolder.websiteLogo.setImageResource(item.getWebsiteLogo());
    }
}
