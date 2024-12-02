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
    private ArrayAdapter<UserProfile> userAdapter;
    private ArrayList<UserProfile> fullUserList;
    private FirebaseFirestore db;
    private String finalUuid;

    public MainActivity main;
    public DBConnection connection;
    public UserDB userDB; // userDB instance for the current user
    public UserProfile user;


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


            userAdapter = new AdminProfileArrayAdapter(getContext(), fullUserList);
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
                        fullUserList.remove(position);
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

    }
    /**
     * Author: Erin-Marie
     * Gets some of the variables from MainActivity that we will need
     */
    public void getVarFromMain() {
        main = (MainActivity) getActivity();
        assert main != null;
        connection = main.connection;
        user = connection.getUser();
        userDB = connection.getUserDB();
    }
}
