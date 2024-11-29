package com.example.myapplication.ui.registeredevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.ui.ViewEventActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Author: Sam Lee
 * Fragment for scanning QR codes.
 * This fragment contains functionality for scanning QR codes and handling the
 * scanned data.
 * [US 01.06.02] As an entrant I want to be able to be sign up for an event by scanning the QR code
 */
public class QRFragment extends Fragment {

    private EventDB eventDB;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize EventDB
        eventDB = new EventDB(new DBConnection(getContext()));

        // Register the launcher and result handler
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), getString(R.string.canceled), Toast.LENGTH_LONG).show();
            } else {
                String eventID = result.getContents();
                // Fetch event details and navigate to event page
                fetchEventDetails(eventID);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_scanner, container, false);

        // Launch the QR scanner
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan a QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);

        barcodeLauncher.launch(options);

        return view;
    }

    /**
     * Author: Sam Lee
     * Fetches event details from the database and navigates to the event page.
     * 
     * @param eventID
     */
    private void fetchEventDetails(String eventID) {
        eventDB.getEvent(eventID, new OnSuccessListener<Event>() {
            @Override
            public void onSuccess(Event event) {
                if (event != null) {
                    // Navigate to event page and pass event details
                    Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                    intent.putExtra("event", event);
                    startActivity(intent);
                } else {
                    // Handle event not found
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}