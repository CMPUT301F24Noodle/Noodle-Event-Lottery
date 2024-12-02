package com.example.myapplication.ui.admin;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.BitmapHelper;
import com.example.myapplication.R;
import com.example.myapplication.objects.UserProfile;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
 * AdminPhotosFragment is responsible for managing user profile images and event posters
 * for administrative purposes. It listens to Firestore for updates, displays user data,
 * and provides functionalities to delete profile pictures.
 *
 * Author: Nishchay Ranjan
 */

public class AdminPhotosFragment extends Fragment {

    private static final String TAG = "AdminPhotosFragment";

    private ListView docRefListView;
    private ArrayList<UserItem> userList;
    private UserAdapter userAdapter;
    private ListenerRegistration listenerRegistrationUsers;
    private ListenerRegistration listenerRegistrationEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_admin_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        docRefListView = view.findViewById(R.id.doc_ref_list_view);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter();
        docRefListView.setAdapter(userAdapter);

        docRefListView.setOnItemClickListener((parent, v, position, id) -> showPopupDialog(userList.get(position)));

        startListeningToFirestoreUsers(); // For user data
        startListeningToFirestoreEvents(); // For event data
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopListeningToFirestore();
    }

    private void startListeningToFirestoreUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        listenerRegistrationUsers = db.collection("AllUsers")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to Firestore changes (Users): ", error);
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

    private void startListeningToFirestoreEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        listenerRegistrationEvents = db.collection("AllEvents")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to Firestore changes (Events): ", error);
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            switch (change.getType()) {
                                case ADDED:
                                    logEventPoster(change);
                                    break;
                                case MODIFIED:
                                    logEventPoster(change);
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Event removed: ID = " + change.getDocument().getId());
                                    break;
                            }
                        }
                    }
                });
    }

    private void stopListeningToFirestore() {
        if (listenerRegistrationUsers != null) {
            listenerRegistrationUsers.remove();
            listenerRegistrationUsers = null;
        }
        if (listenerRegistrationEvents != null) {
            listenerRegistrationEvents.remove();
            listenerRegistrationEvents = null;
        }
    }

    private void addUser(DocumentChange change) {
        String name = change.getDocument().getString("name");
        String uuid = change.getDocument().getString("uuid");
        String encodedPicture = change.getDocument().getString("encodedPicture");

        if (name != null && uuid != null && encodedPicture != null) {
            userList.add(new UserItem(name, uuid, encodedPicture));
            userAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "Skipping user with null encodedPicture: uuid=" + uuid);
        }
    }

    private void logEventPoster(DocumentChange change) {
        String eventName = change.getDocument().getString("eventName");
        String eventId = change.getDocument().getString("eventId");
        String eventPoster = change.getDocument().getString("eventPoster");

        if (eventName != null && eventPoster != null) {
            Log.d(TAG, "Event Poster for Event \"" + eventName + "\" (ID: " + eventId + "): " + eventPoster);
        } else if (eventName != null) {
            Log.d(TAG, "No eventPoster available for Event \"" + eventName + "\" (ID: " + eventId + ")");
        } else {
            Log.e(TAG, "Invalid event data: eventName=" + eventName + ", eventId=" + eventId);
        }
    }

    private void updateUser(DocumentChange change) {
        String uuid = change.getDocument().getString("uuid");
        String name = change.getDocument().getString("name");
        String encodedPicture = change.getDocument().getString("encodedPicture");

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUuid().equals(uuid)) {
                if (encodedPicture != null) {
                    userList.set(i, new UserItem(name, uuid, encodedPicture));
                } else {
                    userList.remove(i);
                }
                userAdapter.notifyDataSetChanged();
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

    private void showPopupDialog(UserItem userItem) {
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.delete_view_images, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(popupView).create();

        ImageView fullProfileImage = popupView.findViewById(R.id.full_profile_image);
        Button deleteButton = popupView.findViewById(R.id.delete_picture_button);
        Button backButton = popupView.findViewById(R.id.picture_back_button);

        try {
            BitmapHelper bitmapHelper = new BitmapHelper();
            UserProfile userProfile = new UserProfile();
            userProfile.setUuid(userItem.getUuid());
            userProfile.setEncodedPicture(userItem.getEncodedPicture());
            userProfile.setHasProfilePic(userItem.getEncodedPicture() != null);

            Bitmap profilePicture = bitmapHelper.loadProfilePicture(userProfile);
            if (profilePicture != null) {
                fullProfileImage.setImageBitmap(profilePicture);
            } else {
                Log.e(TAG, "Failed to load full profile picture for UUID: " + userItem.getUuid());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading full profile picture for UUID: " + userItem.getUuid(), e);
        }

        deleteButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("AllUsers").document(userItem.getUuid())
                    .update("encodedPicture", null)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Profile picture deleted successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).getUuid().equals(userItem.getUuid())) {
                                userList.remove(i);
                                userAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to delete profile picture!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error deleting profile picture: ", e);
                    });
        });

        backButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
                userProfile.setUuid(userItem.getUuid());
                userProfile.setEncodedPicture(userItem.getEncodedPicture());
                userProfile.setHasProfilePic(true);

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
