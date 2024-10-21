package com.example.myapplication.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ListItem> {

    public CustomAdapter(@NonNull Context context, @NonNull List<ListItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ListItem listItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.scanned_list_items, parent, false);
        }

        // Lookup view for data population
        TextView headingView = convertView.findViewById(R.id.heading);
        TextView subheading1View = convertView.findViewById(R.id.subheading1);
        TextView subheading2View = convertView.findViewById(R.id.subheading2);
        TextView subheading3View = convertView.findViewById(R.id.subheading3);

        // Populate the data into the template view using the data object
        headingView.setText(listItem.getHeading());
        subheading1View.setText(listItem.getSubheading1());
        subheading2View.setText(listItem.getSubheading2());
        subheading3View.setText(listItem.getSubheading3());

        // Return the completed view to render on screen
        return convertView;
    }
}
