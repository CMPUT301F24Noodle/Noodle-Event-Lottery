
package com.example.myapplication.ui.myevents;

import android.app.Dialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMyProfileBinding;

public class DisplayQRCodeFragment extends DialogFragment {

    private Bitmap QRCode;

    public void setQRCode(Bitmap QR){
        QRCode = QR;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.qr_view, container, false);


        ImageView qrImageView = view.findViewById(R.id.full_QR_image);
        if (QRCode != null) {
            qrImageView.setImageBitmap(QRCode);
        }


        return view;
    }
}