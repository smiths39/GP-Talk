package com.app.gptalk.custom_adapters;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gptalk.listview_getter.GPAvailableItem;
import com.app.gptalk.R;

public class CustomAvailableGPListViewAdapter extends ArrayAdapter<GPAvailableItem> {

    private Context context;

    public CustomAvailableGPListViewAdapter(Context context, int resourceId, List<GPAvailableItem> items) {

        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {

        ImageView ivProfilePic;
        TextView tvTitle, tvFirstName, tvLastName, tvAddress, tvCity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        GPAvailableItem gpAvailableItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            // Populate retrieved data in listview layout design
            convertView = mInflater.inflate(R.layout.gp_listview_item, null);

            viewHolder = new ViewHolder();
            initialiseWidgets(viewHolder, convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setWidgetResources(viewHolder, gpAvailableItem);

        return convertView;
    }

    private void initialiseWidgets(ViewHolder viewHolder, View view) {

        viewHolder.ivProfilePic = (ImageView) view.findViewById(R.id.ivListProfilePic);
        viewHolder.tvTitle = (TextView) view.findViewById(R.id.tvListTitle);
        viewHolder.tvFirstName = (TextView) view.findViewById(R.id.tvListFirstName);
        viewHolder.tvLastName = (TextView) view.findViewById(R.id.tvListLastName);
        viewHolder.tvAddress = (TextView) view.findViewById(R.id.tvListAddress);
        viewHolder.tvCity = (TextView) view.findViewById(R.id.tvListCity);
    }

    // Getter methods are used to populate textviews within listview
    private void setWidgetResources(ViewHolder viewHolder, GPAvailableItem gpAvailableItem) {

        viewHolder.ivProfilePic.setImageBitmap(gpAvailableItem.getProfilePhoto());
        viewHolder.tvTitle.setText(gpAvailableItem.getTitle());
        viewHolder.tvFirstName.setText(gpAvailableItem.getFirstName());
        viewHolder.tvLastName.setText(gpAvailableItem.getLastName());
        viewHolder.tvAddress.setText(gpAvailableItem.getAddress());
        viewHolder.tvCity.setText(gpAvailableItem.getCity());
    }
}