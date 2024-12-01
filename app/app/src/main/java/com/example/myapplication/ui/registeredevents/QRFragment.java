package com.example.myapplication.ui.registeredevents;

import static android.Manifest.permission_group.CAMERA;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private static final String TAG = "QRFragment";
    private static final int REQUEST_CAMERA = 1 ;
    private EventDB eventDB;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private View v;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize EventDB
        eventDB = new EventDB(new DBConnection(getContext()));

//        if(checkPermission())
//        {
//            Toast.makeText(this.getContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
//        }
//        else
//        {
//            requestPermission();
//        }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_scanner, container, false);

//    private boolean checkPermission()
//    {
//        return (ContextCompat.checkSelfPermission(this.getContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
//    }
//
//    private void requestPermission()
//    {
//        ActivityCompat.requestPermissions(this.getActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
//    }
    /**
     * Configures the scanning options and launches the QR scanner.
     * Sets various options such as prompt text, beep sound, and screen orientation lock during scanning.
     */
    private void scanCode() {
        Log.d("QrCode", "scanCode: Setting up scan options");
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        //options.setOrientationLocked(true);

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