package com.example.myapplication.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.EventDB;
import com.example.myapplication.database.NotificationDB;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminProfileFragment extends Fragment {

    private static final String TAG = "AdminProfileFragment";
    private ListView emailListView;
    private ArrayList<String> userList;
    private ArrayAdapter<String> userAdapter;
    private ArrayList<UserProfile> fullUserList;
    private FirebaseFirestore db;
    private String finalUuid;

    public MainActivity main;
    public DBConnection connection;
    public UserDB userDB; // userDB instance for the current user
    public EventDB eventDB;
    public NotificationDB notifDB;
    public String uuid;
    public UserProfile user;
    public Event event;

    public AdminProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_admin_profile, container, false);


        getVarFromMain();

        if ( user.getAdmin() != Boolean.FALSE){
            emailListView = rootView.findViewById(R.id.email_list_view);
            userList = new ArrayList<>();
            fullUserList = new ArrayList<UserProfile>();
            db = FirebaseFirestore.getInstance();


            userAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, userList);
            emailListView.setAdapter(userAdapter);

            emailListView.setOnItemClickListener((parent, view, position, id) -> {
                String userData = userList.get(position);
                showUserDetailsDialog(userData, position);
            });

            fetchUserDetails();

        } else {
            Toast.makeText(getContext(), "You are not an Admin", Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    private void fetchUserDetails() {
        db.collection("AllUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        userList.clear();

                        for (com.google.firebase.firestore.DocumentSnapshot document : task.getResult().getDocuments()) {
                            Log.v(TAG, "gettign documents");
                            if (document.exists()) {
                                fullUserList.add(document.toObject(UserProfile.class));
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
                        }

                        userAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching data: ", task.getException());
                    }
                });
    }

    private void showUserDetailsDialog(String userData, int position) {
        new AlertDialog.Builder(requireContext()) //
                .setTitle("Delete User")
                .setMessage("Are you sure you would like to delete this user?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(requireContext(), "User deleted successfully!", Toast.LENGTH_SHORT).show();
                        UserProfile delUser = fullUserList.get(position);
                        userList.remove(position);
                        assert delUser != null;
                        db.collection("AllUsers").document("User"+delUser.getUuid()).delete();
                        userAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();




//        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.admin_text_delete_view_profile, null);
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
//        builder.setView(dialogView);
//        android.app.AlertDialog dialog = builder.create();
//
//        TextView profileName = dialogView.findViewById(R.id.profile_name);
//        TextView email = dialogView.findViewById(R.id.email);
//        TextView profilePhone = dialogView.findViewById(R.id.profileid);
//        Button saveButton = dialogView.findViewById(R.id.save_button);
//        Button deleteButton = dialogView.findViewById(R.id.delete_button);
//        Button backButton = dialogView.findViewById(R.id.back_button);
//
//
//        String[] userParts = userData.split("\n");
//        for (String part : userParts) {
//            if (part.startsWith("Name: ")) {
//                profileName.setText(part.replace("Name: ", ""));
//            } else if (part.startsWith("Email: ")) {
//                email.setText(part.replace("Email: ", ""));
//            } else if (part.startsWith("Phone: ")) {
//                profilePhone.setText(part.replace("Phone: ", ""));
//            }
//        }

//        String finalUuid = uuid

//        saveButton.setOnClickListener(v -> {
//            String updatedName = profileName.getText().toString().trim();
//            String updatedEmail = email.getText().toString().trim();
//            String updatedPhone = profilePhone.getText().toString().trim();
//
//            if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
//                Toast.makeText(requireContext(), "Name and Email cannot be empty!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (finalUuid != null) {
//                db.collection("AllUsers").document(finalUuid)
//                        .update("name", updatedName, "email", updatedEmail, "phonenumber", updatedPhone)
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(requireContext(), "User updated successfully!", Toast.LENGTH_SHORT).show();
//                            fetchUserDetails();
//                            dialog.dismiss();
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(requireContext(), "Failed to update user.", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Error updating user: ", e);
//                        });
//            } else {
//                Toast.makeText(requireContext(), "Error: UUID is null.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        deleteButton.setOnClickListener(v -> {
//            if (finalUuid != null) {
//                db.collection("AllUsers").document(finalUuid)
//                        .delete()
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(requireContext(), "User deleted successfully!", Toast.LENGTH_SHORT).show();
//                            userList.remove(position);
//                            userAdapter.notifyDataSetChanged();
//                            dialog.dismiss();
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(requireContext(), "Failed to delete user.", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Error deleting user: ", e);
//                        });
//            } else {
//                Toast.makeText(requireContext(), "Error: UUID is null.", Toast.LENGTH_SHORT).show();
//            }
//        });



    }
    /**
     * Author: Erin-Marie
     * Gets some of the variables from MainActivity that we will need
     */
    public void getVarFromMain() {
        main = (MainActivity) getActivity();
        assert main != null;
        connection = main.connection;

        notifDB = connection.getNotifDB();
        user = connection.getUser();
        eventDB = connection.getEventDB();
        userDB = connection.getUserDB();
    }
}
