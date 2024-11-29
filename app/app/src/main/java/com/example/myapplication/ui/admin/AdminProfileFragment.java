package com.example.myapplication.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminProfileFragment extends Fragment {

    private static final String TAG = "AdminProfileFragment";
    private ListView emailListView;
    private ArrayList<String> emailList;
    private ArrayAdapter<String> emailAdapter;

    public AdminProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.item_admin_profile, container, false);

        // Initialize ListView and ArrayList
        emailListView = rootView.findViewById(R.id.email_list_view);
        emailList = new ArrayList<>();

        // Set up the adapter
        emailAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, emailList);
        emailListView.setAdapter(emailAdapter);

        // Fetch and populate emails
        fetchUserEmails();

        return rootView;
    }

    private void fetchUserEmails() {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference the "AllUsers" collection
        db.collection("AllUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Clear existing data to avoid duplicates
                        emailList.clear();

                        // Loop through each document
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Fetch the email field
                            String email = document.getString("email");

                            // Add email to the list if not null
                            if (email != null) {
                                emailList.add(email);
                                Log.d(TAG, "User Email: " + email);
                            } else {
                                Log.d(TAG, "No email found for document: " + document.getId());
                            }
                        }

                        // Notify adapter about data changes
                        emailAdapter.notifyDataSetChanged();
                    } else {
                        // Log error
                        Log.e(TAG, "Error fetching data: ", task.getException());
                    }
                });
    }
}
