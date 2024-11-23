package com.example.myapplication.ui.myevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.objects.userProfileClasses.UserProfile;

import java.util.List;

public class SelectedArrayAdapter extends ArrayAdapter<UserProfile> {

    private Context context;
    private List<UserProfile> participants;
    private OnRemoveClickListener onRemoveClickListener;

    public SelectedArrayAdapter(Context context, List<UserProfile> participants, OnRemoveClickListener listener) {
        super(context, 0, participants);
        this.context = context;
        this.participants = participants;
        this.onRemoveClickListener = listener;
    }

    /**
     * Author: Apoorv
     * getView for the Manage Event Fragment, gets the participant info at position
     * @param position the int position of the participant being viewed
     * @param convertView this is important idk why
     * @param parent the parent view
     * @return view to be used for that notification
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_manage_event_removable_list, parent, false);
        }

        // Get the participant for this position
        UserProfile participant = getItem(position);

        // Set participant name
        TextView userName = convertView.findViewById(R.id.user_name);
        userName.setText(participant.getName());

        // Set the "REMOVE" button
        TextView removeText = convertView.findViewById(R.id.remove_text);
        removeText.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemoveClick(participant);
            }
        });

        return convertView;
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(UserProfile participant);
    }
}