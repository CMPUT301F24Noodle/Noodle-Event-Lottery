package com.example.myapplication.ui.myevents;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.objects.eventClasses.Event;

/**
 * Author Erin-marie
 * Fragment to end an event manually, or view the entrants
 */
public class ManageEventFragment extends Fragment {

    Event event;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        event = (Event) args.getSerializable("event");

    }
}
