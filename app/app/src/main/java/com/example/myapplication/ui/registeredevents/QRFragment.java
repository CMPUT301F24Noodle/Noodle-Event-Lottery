package com.example.myapplication.ui.registeredevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.objects.Event;
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
    private View v;

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
        ImageView cameraView = view.findViewById(R.id.camera_preview);
        cameraView.setVisibility(View.INVISIBLE); // because we didn't use this and its too late

        Button scanButton = view.findViewById(R.id.scan_qr);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Launch the QR scanner
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("Scan a QR code");
                options.setCameraId(0);
                options.setBeepEnabled(true);
                options.setBarcodeImageEnabled(true);

                barcodeLauncher.launch(options);
            }
        });

        // Launch the QR scanner
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan a QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);

        barcodeLauncher.launch(options);

        v = view;
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
                    MainActivity main = (MainActivity) getActivity();
                    if (main != null) {
                        main.scannedEvent = event;
                    }
                    // Navigate to event page
                    Navigation.findNavController(v).navigate(R.id.nav_view_scanned_event);
                } else {
                    // Handle event not found
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}