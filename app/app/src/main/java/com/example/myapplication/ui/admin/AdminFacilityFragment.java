package com.example.myapplication.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.FacilityDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminFacilityFragment extends Fragment {

    private static final String TAG = "AdminFacilityFragment";

    private ListView facilityListView;
    private ArrayList<Facility> facilityList;
    private AdminFacilityArrayAdapter facilityAdapter;
    private FacilityDB facilityDB;
    DBConnection connection;


    public AdminFacilityFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_admin_listview, container, false);

        facilityListView = rootView.findViewById(R.id.admin_item_list);
        facilityList = new ArrayList<>();

        facilityAdapter = new AdminFacilityArrayAdapter(requireContext(), 0, facilityList);

        facilityListView.setAdapter(facilityAdapter);

        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
           connection = main.connection;
            this.facilityDB = connection.getFacilityDB();
        }

        fetchAllFacilities();

        facilityListView.setOnItemClickListener((parent, view, position, id) -> {
            Facility facility = facilityList.get(position);
            deleteFacilitiesDialog(position);
        });

     //
        return rootView;
    }

    /**
     * Method that gets all of the facilities from the db
     */
    private void fetchAllFacilities() {
        connection.getDB().collection("AllFacilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        facilityList.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            facilityList.add(document.toObject(Facility.class));
                            Log.d(TAG, "Facility added");
                        }
                        facilityAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching events: ", task.getException());
                    }
                });
    }

    private void deleteFacilitiesDialog(int position) {
        new AlertDialog.Builder(requireContext()) //
                .setTitle("Delete Facility")
                .setMessage("Are you sure you would like to delete this facility?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(requireContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                        facilityDB.deleteFacility(facilityList.get(position));
                        facilityList.remove(position);
                        facilityAdapter.notifyDataSetChanged();

                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
