package com.example.myapplication.ui.myevents.manageEvent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.objects.Notification;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.firebase.firestore.DocumentReference;

/**
 * Author Erin-marie
 * Fragment to end an event manually, and view the entrants and selected attendees
 * TODO: add functionality to the replace cancelled button
 * TODO: write tests
 */
public class ManageEventFragment extends Fragment {

    Event event;
    EventDB eventDB;
    DBConnection connection;
    NotificationDB notifDB;
    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    ArrayList<String> expandableListTitle;
    HashMap<String, ArrayList<UserProfile>> expandableListDetail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.org_manage_event, container, false);

        Bundle args = getArguments();
        assert args != null;
        event = (Event) args.getSerializable("event");
        eventDB = (EventDB) args.get("eventDB");
        connection = eventDB.getConnection();
        notifDB = connection.getNotifDB();
        assert eventDB != null;


        //initialize buttons
        FloatingActionButton viewMapButton = view.findViewById(R.id.FAB_map);
        FloatingActionButton backButton = view.findViewById(R.id.FAB_back);
        Button sendMessageButton = view.findViewById((R.id.notify_button));
        Button replaceButton = view.findViewById(R.id.replace_button);
        Button endLotteryButton = view.findViewById(R.id.draw_name_button);

        GetListData listPopulator = new GetListData(eventDB);

        //initialize the expandable list view
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        //get the list data
        expandableListDetail = listPopulator.getData();
        //set the title for each list group
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        //set the list adapter
        expandableListAdapter = new CustomExpandableListAdapter(this.getContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        //listener for expanding a list group
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getContext(), expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

//        //listener for collapsing a list group
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//
//
//            }
//        });

        //listener for selecting a list item
        //If a user item is selected, will open a dialog to remove the user from the event, and notify the user that they have been removed
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                if (groupPosition != 3 && groupPosition != 1) { //only declined users or users who won but have not accepted their invitation can be removed
                    Toast.makeText(getContext(),
                            "Users in the " + expandableListTitle.get(groupPosition) + " list cannot be removed.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // create a dialogue box for them to confirm their choice
                    new AlertDialog.Builder(requireContext()) //
                            .setTitle("Remove User")
                            .setMessage("Are you sure you would like to remove this user from the list?")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserProfile user = expandableListAdapter.deleteChild(groupPosition, childPosition);
                                    DocumentReference userRef = user.getDocRef();
                                    ArrayList<DocumentReference> recipient = new ArrayList<>();
                                    recipient.add(userRef);

                                    //remove the user from the events declined list
                                    event.getDeclinedList().remove(userRef);
                                    event.getWinnersList().remove(userRef);
                                    eventDB.updateEvent(event);
                                    expandableListAdapter.notifyDataSetChanged();

                                    //send a notification to the cancelled user
                                    Notification notif = new Notification("Event Status Change", "You have been removed from " + event.getEventName() + " event.", recipient, event.getOrganizer());
                                    notifDB.addNotification(notif);
                                }
                            })
                            .setNegativeButton("Cancel", null) // closes the dialog on cancel
                            .show();


                }
                return true;
            }
        });


        //When the organizer presses the send message button, they will get an alert dialog to write a custom message that all entrants will recieve as a notification
        sendMessageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    // Create the object of AlertDialog Builder class
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    // Set the message show for the Alert time
                    builder.setMessage("Send a message to all entrants");

                    // Set Alert Title
                    builder.setTitle("Type a Message");
                    final EditText message = new EditText(getContext());
                    builder.setView(message);

                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(false);

                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setPositiveButton("Send", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // When the user click yes button then app will close
                        assert event.getOrganizer() != null;
                        Notification notif = new Notification("Announcement", message.getText().toString().trim(), event.getEntrantsList(), event.getOrganizer());
                        notifDB.addNotification(notif);
                        Log.v("MessageDialog", "Notification sent to all entrants of event");
                    });

                    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.
                        dialog.cancel();
                    });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();
                }
            }
        });

        //onCLick for when the endLottery button is selected
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //onCLick for when the endLottery button is selected
        endLotteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventDB.endEvent(event);
                CharSequence text = "Event lottery ended, notifications have been sent to entrants.";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

            }
        });


        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = "Replacement winners have been selected and notified";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                int usersNeeded = event.getUsersNeededCount(); //TODO write this method that will return the number of users that need to be selected to make up for declined users
                //get replacement winners and notify them
                eventDB.getRandomWinners(event.getLosersList(), usersNeeded, event);
                //this will notify all the new winners, and also send another notification to any users who have no accepted yet
                eventDB.sendMessageToWinners(event);
                //remove all of the declined users and notify them
                removeDeclinedUsers();

            }
        });

        //Button for viewing the GeoLocation map of all entrants for the event
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the map fragment already exists
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("map_fragment");

                CharSequence text = "Opening the map... This will take a while";
                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();

                if (mapFragment == null) {
                    // map fragment doesn't exist, create and add it
                    mapFragment = SupportMapFragment.newInstance();
                    Bundle args = new Bundle();

                    // default location: edmonton
                    double defaultLatitude = 53.5461;
                    double defaultLongitude = -113.4937;
                    int zoomLevel = 12;

                    args.putDouble("latitude", defaultLatitude);
                    args.putDouble("longitude", defaultLongitude);
                    args.putInt("zoomLevel", zoomLevel);

                    mapFragment.setArguments(args);

                    // add the map fragment dynamically
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.manage_event, mapFragment, "map_fragment")
                            .commit();

                    // update the camera once ready
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            double edmontonLatitude = 53.5461;
                            double edmontonLongitude = -113.4937;
                            int zoomLevel = 12;

                            // camera position
                            LatLng edmonton = new LatLng(edmontonLatitude, edmontonLongitude);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, zoomLevel));

                        }
                    });

                } else {
                    // map fragment exists, remove it
                    getChildFragmentManager().beginTransaction()
                            .remove(mapFragment)
                            .commit();
                }
            }
        });

        return view;
    }


    /**
     * Method to remove ALL declined users from the event
     * Called before drawing new winners
     */
    public void removeDeclinedUsers() {
        ArrayList<DocumentReference> recipients = new ArrayList<>(event.getDeclinedList());

        //clear all of the declined users
        event.getDeclinedList().clear();
        eventDB.updateEvent(event);

        //send a notification to all of the declined users
        Notification notif = new Notification("Event Status Change", "Since you declined your invitation, you have been removed from " + event.getEventName() + "event.", recipients, event.getOrganizer());
        notifDB.addNotification(notif);

    }

}
