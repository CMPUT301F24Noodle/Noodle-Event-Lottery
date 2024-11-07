package com.example.myapplication.ui.myevents;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.myapplication.objects.eventClasses.Event;

import java.util.ArrayList;

//I am just adding this for testing, it is not actually functional, i just needed it so that i can write the MainActivity navigate test
//Nischay can replace this
public class OrganizedEventArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;

    public OrganizedEventArrayAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }
}
