package com.example.myapplication.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private ArrayList<String> userList;
    private ArrayAdapter<String> userAdapter;
    private FirebaseFirestore db;

    public AdminProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_admin_profile, container, false);

        emailListView = rootView.findViewById(R.id.email_list_view);
        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        userAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, userList);
        emailListView.setAdapter(userAdapter);

        emailListView.setOnItemClickListener((parent, view, position, id) -> {
            String userData = userList.get(position);
            showUserDetailsDialog(userData, position);
        });

        fetchUserDetails();

        return rootView;
    }

    private void fetchUserDetails() {
        db.collection("AllUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        userList.clear();

                        for (com.google.firebase.firestore.DocumentSnapshot document : task.getResult().getDocuments()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String phoneNumber = document.getString("phonenumber");
                            String uuid = document.getId();

                            StringBuilder userInfo = new StringBuilder();
                            userInfo.append("Name: ").append(name != null ? name : "N/A").append("\n");
                            userInfo.append("Email: ").append(email != null ? email : "N/A").append("\n");
                            userInfo.append("Phone: ").append(phoneNumber != null ? phoneNumber : "N/A").append("\n");
                            userInfo.append("UUID: ").append(uuid);

                            userList.add(userInfo.toString());
                        }

                        userAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching data: ", task.getException());
                    }
                });
    }

    private void showUserDetailsDialog(String userData, int position) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.admin_text_delete_view_profile, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        EditText profileName = dialogView.findViewById(R.id.profile_name);
        EditText email = dialogView.findViewById(R.id.email);
        EditText profilePhone = dialogView.findViewById(R.id.profileid);
        Button saveButton = dialogView.findViewById(R.id.save_button);
        Button deleteButton = dialogView.findViewById(R.id.delete_button);
        Button backButton = dialogView.findViewById(R.id.back_button);

        String uuid = null;
        String[] userParts = userData.split("\n");
        for (String part : userParts) {
            if (part.startsWith("Name: ")) {
                profileName.setText(part.replace("Name: ", ""));
            } else if (part.startsWith("Email: ")) {
                email.setText(part.replace("Email: ", ""));
            } else if (part.startsWith("Phone: ")) {
                profilePhone.setText(part.replace("Phone: ", ""));
            } else if (part.startsWith("UUID: ")) {
                uuid = part.replace("UUID: ", "").trim();
            }
        }

        final String finalUuid = uuid;

        saveButton.setOnClickListener(v -> {
            String updatedName = profileName.getText().toString().trim();
            String updatedEmail = email.getText().toString().trim();
            String updatedPhone = profilePhone.getText().toString().trim();

            if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Name and Email cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (finalUuid != null) {
                db.collection("AllUsers").document(finalUuid)
                        .update("name", updatedName, "email", updatedEmail, "phonenumber", updatedPhone)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "User updated successfully!", Toast.LENGTH_SHORT).show();
                            fetchUserDetails();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to update user.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error updating user: ", e);
                        });
            } else {
                Toast.makeText(requireContext(), "Error: UUID is null.", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (finalUuid != null) {
                db.collection("AllUsers").document(finalUuid)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "User deleted successfully!", Toast.LENGTH_SHORT).show();
                            userList.remove(position);
                            userAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to delete user.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error deleting user: ", e);
                        });
            } else {
                Toast.makeText(requireContext(), "Error: UUID is null.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}