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

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;

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
    ExpandableListAdapter expandableListAdapter;
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
        assert eventDB != null;


        //initialize buttons
        FloatingActionButton viewMapButton = view.findViewById(R.id.FAB_map);
        FloatingActionButton backButton = view.findViewById(R.id.FAB_back);
        Button sendMessageButton = view.findViewById((R.id.notify_button));
        Button replaceButton = view.findViewById(R.id.replace_button);
        Button endLotteryButton = view.findViewById(R.id.draw_name_button);

        GetListData listPopulator = new GetListData(eventDB);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListDetail = listPopulator.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this.getContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getContext(), expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });


//        TextView currentWaitView = view.findViewById(R.id.nav_waiting);
//        ListView waitingListView = view.findViewById(R.id.nav_waiting_list);
//
//        //TextView selectedView = view.findViewById(R.id.nav_selected);
//
//        ExpandableListView expandableListView = view.findViewById(R.id.selected_ppl);
//
//        TextView cancelledView = view.findViewById(R.id.nav_cancelled);
//        ListView cancelledListView = view.findViewById(R.id.cancelled_ppl);
//
//        TextView attendingView = view.findViewById(R.id.nav_attending);
//        ListView attendingListView = view.findViewById(R.id.attending_ppl);




//        String text;
//        if (event.getMaxEntrants().equals(-1)){
//            text = "Current waiting list: " + event.getWaitingListSize() + " entrants";
//            currentWaitView.setText(text);
//        } else {
//            text = "Current Waiting List: " + event.countEntrants().toString() + " / " + event.getMaxEntrants();
//            currentWaitView.setText(text);
//        }
//
//        //if the event has ended, include the length of each list in the text view
//        if(event.getEventOver() == Boolean.TRUE){
//            String selectedText = "People Selected: " + event.getWinnersList().size();
//            //selectedView.setText(selectedText);
//
//            String cancelledText = "People Cancelled: " + event.getDeclinedList().size();
//            cancelledView.setText(cancelledText);
//
//            String attendingText = "People Attending: " + event.getAcceptedList().size();
//            attendingView.setText(attendingText);
//
//        }






//        EntrantArrayAdapter waitAdapter= new EntrantArrayAdapter(getContext(), entrants);
//        waitingListView.setAdapter(waitAdapter);
//
//        //All Selected (Winners)
//
//        EntrantArrayAdapter selectedAdapter = new EntrantArrayAdapter(getContext(), winners);
//        selectedListView.setAdapter(selectedAdapter);
//
//        //All Cancelled (declined)
//        EntrantArrayAdapter declinedAdapter = new EntrantArrayAdapter(getContext(), declined);
//        cancelledListView.setAdapter(declinedAdapter);
//
//        //All Attending (accepted)
//        EntrantArrayAdapter acceptedAdapter = new EntrantArrayAdapter(getContext(), accepted);
//        attendingListView.setAdapter(acceptedAdapter);




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
//                waitAdapter.notifyDataSetChanged();
//                selectedAdapter.notifyDataSetChanged();
//                declinedAdapter.notifyDataSetChanged();
//                acceptedAdapter.notifyDataSetChanged();

            }
        });

       // TODO: make this actually work
        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = "This feature is not available yet, sorry";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
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

                            // marker at edmonton
//                            googleMap.addMarker(new MarkerOptions()
//                                    .position(edmonton)
//                                    .title("Edmonton"));
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

//
//        SupportMapFragment mapFragment = (SupportMapFragment) getParentFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        return view;
    }

//    public HashMap<String, ArrayList<UserProfile>> getData(){
//        HashMap<String, ArrayList<UserProfile>> expandableListDetail = new HashMap<String, ArrayList<UserProfile>>();
//        //All Waitlisted (Entrants)
//        ArrayList<UserProfile> entrants = eventDB.getEntrantsList();
//        ArrayList<UserProfile> winners = eventDB.getWinnersList();
//        ArrayList<UserProfile> declined = eventDB.getDeclinedList();
//        ArrayList<UserProfile> accepted = eventDB.getAcceptedList();
//        Log.v("getData", "size of entrants list: " + entrants.size());
//
//        expandableListDetail.put("Entrants", entrants);
//        expandableListDetail.put("Winners", winners);
//        expandableListDetail.put("Declined", declined);
//        expandableListDetail.put("Accepted", accepted);
//
//        Log.v("getData", "size of entrants list: " + entrants.size());
//
//        return expandableListDetail;
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        // Do something
//    }

}
