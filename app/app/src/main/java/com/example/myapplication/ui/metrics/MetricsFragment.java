package com.example.myapplication.ui.metrics;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.databinding.FragmentUserMetricsBinding;
import com.example.myapplication.objects.UserProfile;

import de.hdodenhof.circleimageview.CircleImageView;

public class MetricsFragment extends Fragment {
    private FragmentUserMetricsBinding binding;
    UserProfile user;
    BitmapHelper bitmapper = new BitmapHelper();
    private MetricsHelper metric;
    public DBConnection connection;
    public EventDB eventDB;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserMetricsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        MainActivity main = (MainActivity) requireActivity();
        connection = main.connection;
        eventDB = connection.getEventDB();
        user = main.user;

        TextView usernameText = view.findViewById(R.id.metric_username);
        if (user.getName() != null){
            usernameText.setText(user.getName());
        } else {
        usernameText.setText("Your Metrics");
        }

        Bitmap pfp = bitmapper.loadProfilePicture(user);
        CircleImageView profileImage = view.findViewById(R.id.metric_image);
        profileImage.setImageBitmap(pfp);

        metric = new MetricsHelper();

        TextView enteredEvent = view.findViewById(R.id.metric_entered);
        enteredEvent.setText("Events Currently Entered: "+metric.getNumberOfEnteredEvents(user));

        TextView enteredLife = view.findViewById(R.id.metric_entered_life);
        enteredLife.setText("Events Entered (Lifetime): "+metric.getNumberOfLiftimeEvents(user, eventDB));

        TextView successRate = view.findViewById(R.id.metric_success);
        successRate.setText("Success Rate: "+String.format("%.2f%%", metric.getSuccessRate(user, eventDB)));

        TextView pastaRatioText = view.findViewById(R.id.metric_pasta);
        pastaRatioText.setText("Name to Noodle Ratio: " + String.format("%.2f%%", metric.calculatePastaRatio(user)));

        //Show the organizer Section only if they are one.
        View OrgView = view.findViewById(R.id.metric_organizer);
        if (user.checkIsOrganizer() == Boolean.TRUE){
            OrgView.setVisibility(View.VISIBLE);

            TextView eventsCreatedText = view.findViewById(R.id.metric_created);
            eventsCreatedText.setText("Number of Events Created: "+metric.getNumberOfCreatedEvents(user, eventDB));

            TextView eventsEnteredText = view.findViewById(R.id.metric_org_enter);
            eventsEnteredText.setText("Number of Applicants: "+metric.getTotalEntrants(user, eventDB));

            TextView eventsParticipantsText = view.findViewById(R.id.metric_org_winner);
            eventsParticipantsText.setText("Number of Participants: "+metric.getTotalWinners(user, eventDB));

            TextView eventsAverageEntrantText = view.findViewById(R.id.metric_org_avgenter);
            eventsAverageEntrantText.setText("Average Entrants Per Event: "+String.format("%.2f",metric.getAverageEntrants(user, eventDB)));

            TextView eventsAverageDeclineText = view.findViewById(R.id.metric_org_avgdecline);
            eventsAverageDeclineText.setText("Average Declines Per Event: "+String.format("%.2f",metric.getAverageDeclines(user, eventDB)));

            TextView eventsDeclineRateText = view.findViewById(R.id.metric_org_decrate);
            eventsDeclineRateText.setText("Average Declines Rate: "+String.format("%.2f%%",metric.getDeclineRate(user, eventDB)));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up the binding reference
        binding = null;
    }
}
