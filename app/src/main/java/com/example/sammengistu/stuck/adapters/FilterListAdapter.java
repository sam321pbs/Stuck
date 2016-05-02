package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SamMengistu on 5/2/16.
 */
public class FilterListAdapter extends ArrayAdapter<String> {

    private List<String> mFilteredListItems;
    private Activity mMainListActivity;

    public FilterListAdapter(List<String> filterItems, Activity activity) {
        super(activity, 0, filterItems);
        mMainListActivity = activity;
        mFilteredListItems = filterItems;
        mFilteredListItems.add(0, "My Posts");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = mMainListActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.filtered_list_item, parent, false);

        }

        TextView filteredItemText = (TextView) convertView.findViewById(R.id.filtered_list_item_text);

        filteredItemText.setText(mFilteredListItems.get(position));

        if (position == 0){
            filteredItemText.setTextColor(Color.BLUE);
        } else {
            filteredItemText.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}
