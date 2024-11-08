package com.example.myapplication.ui.myevents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.objects.eventClasses.Event;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.example.myapplication.ui.notifications.Notification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Author Erin-marie
 * Fragment to end an event manually, and view the entrants and selected attendees
 * TODO: add functionality to the map button and the replace cancelled button
 * TODO: add lists for the people who cancelled and the people who have accepted their invitation
 */
public class ManageEventFragment extends Fragment {

    Event event;
    EventDB eventDB;
    DBConnection connection;
    NotificationDB notifDB;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.org_manage_event, container, false);

        connection = new DBConnection(getContext());
        notifDB = connection.getNotifDB();

        Bundle args = getArguments();
        assert args != null;
        event = (Event) args.getSerializable("event");
        eventDB = (EventDB) args.get("eventDB");
        assert eventDB != null;


        TextView currentWaitView = view.findViewById(R.id.nav_waiting);
        ListView waitingListView = view.findViewById(R.id.nav_waiting_list);

        TextView selectedView = view.findViewById(R.id.nav_selected);
        ListView selectedListView = view.findViewById(R.id.selected_ppl);

        TextView cancelledView = view.findViewById(R.id.nav_cancelled);
        ListView cancelledListView = view.findViewById(R.id.cancelled_ppl);

        TextView attendingView = view.findViewById(R.id.nav_attending);
        ListView attendingListView = view.findViewById(R.id.attending_ppl);

        //initialize buttons
        FloatingActionButton viewMapButton = view.findViewById(R.id.FAB_map);
        Button sendMessageButton = view.findViewById((R.id.notify_button));
        Button replaceButton = view.findViewById(R.id.replace_button);
        Button endLotteryButton = view.findViewById(R.id.draw_name_button);


        String text;
        if (event.getMaxEntrants() == -1){
            text = "Current waiting list: " + event.getWaitingListSize() + " entrants";
            currentWaitView.setText(text);
        } else {
            text = "Current Waiting List: " + event.countEntrants().toString() + " / " + event.getMaxEntrants();
            currentWaitView.setText(text);
        }

        //All Entrants
        eventDB.getEventEntrants(event);
        ArrayList<UserProfile> entrants = eventDB.getEntrantsList();

        EntrantArrayAdapter waitAdapter= new EntrantArrayAdapter(getContext(), entrants);
        waitingListView.setAdapter(waitAdapter);

        eventDB.getEventWinners(event);

        //If the event is over then show the winners list

            ArrayList<UserProfile> winners = eventDB.getWinnersList();

            EntrantArrayAdapter selectedAdapter= new EntrantArrayAdapter(getContext(), winners);
            selectedListView.setAdapter(selectedAdapter);

        //TODO add accepted list and declined list





        /**
         * When the organizer presses the send message button, they will get an alert dialog to write a custom message that all entrants will recieve as a notification
         */
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
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.
                        dialog.cancel();
                    });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();
                }
            }
        }
        );

        //onCLick for when the endLottery button is selected
        endLotteryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                eventDB.endEvent(event);
                CharSequence text = "Event lottery ended, notifications have been sent to entrants.";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                waitAdapter.notifyDataSetChanged();
                selectedAdapter.notifyDataSetChanged();

            }
        });



       // TODO: make these actually work
        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = "This feature is not available yet, sorry";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = "This feature is not available yet, sorry";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


}
