package com.example.myapplication.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class QRFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Replace "R.layout.your_qr_layout" with the actual name of your XML file for the QR scanner.
        return inflater.inflate(R.layout.qr_scanner, container, false);
    }
}