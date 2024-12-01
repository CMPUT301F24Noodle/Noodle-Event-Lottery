package com.example.myapplication.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminProfileFragment extends Fragment {

    private static final String TAG = "AdminProfileFragment";
    private ListView emailListView;
    private ArrayList<String> userList; // Stores user details
    private ArrayAdapter<String> userAdapter;

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
        userList = new ArrayList<>();

        // Set up the adapter
        userAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, userList);
        emailListView.setAdapter(userAdapter);

        // Set click listener for list items
        emailListView.setOnItemClickListener((parent, view, position, id) -> {
            String userData = userList.get(position); // Get the clicked user data
            showUserDetailsDialog(userData); // Display the user details in a dialog
        });

        // Fetch and populate user details
        fetchUserDetails();

        return rootView;
    }

    private void fetchUserDetails() {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference the "AllUsers" collection
        db.collection("AllUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Clear existing data to avoid duplicates
                        userList.clear();

                        // Loop through each document
                        for (com.google.firebase.firestore.DocumentSnapshot document : task.getResult().getDocuments()) {
                            // Fetch name, email, and phonenumber fields
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String phoneNumber = document.getString("phonenumber");

                            // Create a string to represent the user's details
                            StringBuilder userInfo = new StringBuilder();
                            if (name != null) {
                                userInfo.append("Name: ").append(name).append("\n");
                            } else {
                                userInfo.append("Name: N/A\n");
                            }

                            if (email != null) {
                                userInfo.append("Email: ").append(email).append("\n");
                            } else {
                                userInfo.append("Email: N/A\n");
                            }

                            if (phoneNumber != null) {
                                userInfo.append("Phone: ").append(phoneNumber);
                            } else {
                                userInfo.append("Phone: N/A");
                            }

                            // Add the user info to the list
                            userList.add(userInfo.toString());
                            Log.d(TAG, "User Details: " + userInfo);
                        }

                        // Notify adapter about data changes
                        userAdapter.notifyDataSetChanged();
                    } else {
                        // Log error
                        Log.e(TAG, "Error fetching data: ", task.getException());
                    }
                });
    }

    private void showUserDetailsDialog(String userData) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.admin_text_delete_view_profile, null);

        // Create the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        // Bind views in the dialog
        TextView profileName = dialogView.findViewById(R.id.profile_name);
        TextView email = dialogView.findViewById(R.id.email);
        TextView profileId = dialogView.findViewById(R.id.profileid);
        TextView roles = dialogView.findViewById(R.id.roles); // Placeholder
        Button editButton = dialogView.findViewById(R.id.edit_button);
        Button deleteButton = dialogView.findViewById(R.id.delete_button);
        Button backButton = dialogView.findViewById(R.id.back_button);

        // Parse user data from the selected item
        String[] userParts = userData.split("\n");
        for (String part : userParts) {
            if (part.startsWith("Name: ")) {
                profileName.setText(part.replace("Name: ", ""));
            } else if (part.startsWith("Email: ")) {
                email.setText(part.replace("Email: ", ""));
            } else if (part.startsWith("Phone: ")) {
                profileId.setText(part.replace("Phone: ", ""));
            }
        }
        roles.setText("N/A"); // Set default or dynamic value for roles

        // Edit button functionality (currently placeholder)
        editButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit functionality not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        // Delete button functionality
        deleteButton.setOnClickListener(v -> {
            // Logic to delete user (can be implemented based on Firestore logic)
            Toast.makeText(requireContext(), "Delete functionality not implemented yet.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Back button functionality
        backButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

}
