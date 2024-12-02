package com.example.myapplication.ui.myevents.manageEvent;

import static androidx.navigation.ui.NavigationUI.navigateUp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
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

import com.example.myapplication.MainActivity;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * Fragment to manage an event
 * end an event manually, view the entrants and selected attendees
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

        // Retrieve instances from MainActivity
        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            this.connection = main.connection;
            this.eventDB = main.eventDB;
            this.event = main.currentEvent;
        }
        notifDB = connection.getNotifDB();
        assert eventDB != null;

        //initialize buttons
        FloatingActionButton viewMapButton = view.findViewById(R.id.FAB_map);
        FloatingActionButton backButton = view.findViewById(R.id.FAB_back);
        Button sendMessageButton = view.findViewById((R.id.notify_button));
        Button replaceButton = view.findViewById(R.id.replace_button);
        Button endLotteryButton = view.findViewById(R.id.draw_name_button);

        if (event.getEventOver() == Boolean.TRUE){
            endLotteryButton.setVisibility(View.INVISIBLE);
        }

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

        Button exportCsvButton = view.findViewById(R.id.export_csv_button);
        exportCsvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Define the CSV file path
                    File csvFile = new File(getContext().getExternalFilesDir(null), "event_details.csv");
                    FileWriter writer = new FileWriter(csvFile);

                    // Add headers
                    writer.append("S. No.,Name,Email Address,Contact Number\n");

                    // Write data for each list
                    int serialNo = 1;
                    for (String group : expandableListTitle) {
                        ArrayList<UserProfile> userList = expandableListDetail.get(group);
                        if (userList != null) {
                            writer.append(group).append("\n");
                            for (UserProfile user : userList) {
                                writer.append((char) serialNo++).append(",");
                                writer.append(user.getName() != null ? user.getName() : "NA").append(",");
                                writer.append(user.getEmail() != null ? user.getEmail() : "NA").append(",");
                                writer.append(user.getPhoneNumber() != null ? user.getPhoneNumber() : "NA").append("\n");
                            }
                        }
                    }

                    writer.flush();
                    writer.close();

                    // Notify user of success
                    Toast.makeText(getContext(), "CSV Exported to: " + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error exporting CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
                getActivity().onBackPressed();
            }
        });

        //onCLick for when the endLottery button is selected
        endLotteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.getEventOver() != Boolean.TRUE) {
                    eventDB.endEvent(event);
                    endLotteryButton.setVisibility(View.INVISIBLE);
                    CharSequence text = "Event lottery ended, notifications have been sent to entrants.";
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                } else {
                    CharSequence text = "Event lottery has already been ended";
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });


        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int usersNeeded = event.getUsersNeededCount();
                //get replacement winners and notify them
                eventDB.getRandomWinners(event.getLosersList(), usersNeeded, event);
                //this will notify all the new winners, and also send another notification to any users who have no accepted yet
                eventDB.sendMessageToWinners(event);
                //remove all of the declined users and notify them
                removeDeclinedUsers();
                expandableListAdapter.notifyDataSetChanged();


                //print a toast
                CharSequence text = usersNeeded + " Replacement winners have been selected and notified";
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
        eventDB.getEventWinners(event);
        expandableListAdapter.notifyDataSetChanged();

        //send a notification to all of the declined users
        Notification notif = new Notification("Event Status Change", "Since you declined your invitation, you have been removed from " + event.getEventName() + "event.", recipients, event.getOrganizer());
        notifDB.addNotification(notif);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
