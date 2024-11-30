package com.example.myapplication.ui.admin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.R;
import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class AdminPhotosFragment extends Fragment {

    private static final String TAG = "AdminPhotosFragment";

    private ListView docRefListView;
    private ArrayList<UserItem> userList;
    private UserAdapter userAdapter;
    private ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_admin_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ListView and Adapter
        docRefListView = view.findViewById(R.id.doc_ref_list_view);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter();
        docRefListView.setAdapter(userAdapter);

        // Start listening to Firestore
        startListeningToFirestore();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopListeningToFirestore();
    }

    private void startListeningToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        listenerRegistration = db.collection("AllUsers")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to Firestore changes: ", error);
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            switch (change.getType()) {
                                case ADDED:
                                    addUser(change);
                                    break;

                                case MODIFIED:
                                    updateUser(change);
                                    break;

                                case REMOVED:
                                    removeUser(change);
                                    break;
                            }
                        }
                    }
                });
    }

    private void stopListeningToFirestore() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    private void addUser(DocumentChange change) {
        String name = change.getDocument().getString("name");
        String uuid = change.getDocument().getString("uuid");
        String encodedPicture = change.getDocument().getString("encodedPicture");

        if (name != null && uuid != null) {
            userList.add(new UserItem(name, uuid, encodedPicture));
            userAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Invalid user data: name=" + name + ", uuid=" + uuid);
        }
    }

    private void updateUser(DocumentChange change) {
        String uuid = change.getDocument().getString("uuid");

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUuid().equals(uuid)) {
                String name = change.getDocument().getString("name");
                String encodedPicture = change.getDocument().getString("encodedPicture");

                if (name != null) {
                    userList.set(i, new UserItem(name, uuid, encodedPicture));
                    userAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Invalid updated user data: uuid=" + uuid);
                }
                return;
            }
        }
    }

    private void removeUser(DocumentChange change) {
        String uuid = change.getDocument().getString("uuid");

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUuid().equals(uuid)) {
                userList.remove(i);
                userAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private class UserAdapter extends ArrayAdapter<UserItem> {

        public UserAdapter() {
            super(requireContext(), R.layout.item_admin_images, userList);
        }

        @Override
        @NonNull
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_images, parent, false);
            }

            UserItem userItem = userList.get(position);

            TextView usernameTextView = convertView.findViewById(R.id.username);
            TextView logtimeTextView = convertView.findViewById(R.id.logtime);
            ImageView eventPosterImageView = convertView.findViewById(R.id.event_poster);

            usernameTextView.setText(userItem.getName());
            logtimeTextView.setText(userItem.getUuid());

            try {
                BitmapHelper bitmapHelper = new BitmapHelper();
                UserProfile userProfile = new UserProfile();

                // Safely set UUID and encoded picture
                userProfile.setUuid(userItem.getUuid());
                userProfile.setEncodedPicture(userItem.getEncodedPicture());
                userProfile.setHasProfilePic(userItem.getEncodedPicture() != null); // Example logic

                Bitmap profilePicture = bitmapHelper.loadProfilePicture(userProfile);
                if (profilePicture != null) {
                    eventPosterImageView.setImageBitmap(profilePicture);
                } else {
                    Log.e(TAG, "Profile picture is null for UUID: " + userItem.getUuid());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading profile picture for UUID: " + userItem.getUuid(), e);
            }

            return convertView;
        }
    }

    private static class UserItem {
        private final String name;
        private final String uuid;
        private final String encodedPicture;

        public UserItem(String name, String uuid, String encodedPicture) {
            this.name = name;
            this.uuid = uuid;
            this.encodedPicture = encodedPicture;
        }

        public String getName() {
            return name;
        }

        public String getUuid() {
            return uuid;
        }

        public String getEncodedPicture() {
            return encodedPicture;
        }
    }
}
