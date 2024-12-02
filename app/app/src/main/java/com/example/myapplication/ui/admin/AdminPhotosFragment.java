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
 * AdminPhotosFragment
 *
 * This fragment is designed to display and manage user profile pictures for administrative purposes.
 * Admins can view a list of users, display their profile pictures, and delete pictures if necessary.
 * The data is fetched in real-time from Firestore, and the UI updates dynamically based on changes.
 *
 * Author: Nishchay Ranjan
 */
public class AdminPhotosFragment extends Fragment {

    private static final String TAG = "AdminPhotosFragment"; // Log tag for debugging

    private ListView docRefListView; // ListView to display user profiles
    private ArrayList<UserItem> userList; // List of UserItem objects representing user profiles
    private UserAdapter userAdapter; // Custom adapter for the ListView
    private ListenerRegistration listenerRegistration; // Firestore listener for real-time updates

    /**
     * onCreateView
     *
     * Inflates the fragment layout.
     *
     * @param inflater  The LayoutInflater object to inflate views
     * @param container The parent container view
     * @param savedInstanceState Previous saved instance state
     * @return Inflated view for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_admin_images, container, false);
    }

    /**
     * onViewCreated
     *
     * Sets up the ListView adapter, initializes Firestore listener, and handles item click events.
     *
     * @param view Root view of the fragment
     * @param savedInstanceState Previous saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        docRefListView = view.findViewById(R.id.doc_ref_list_view);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter();
        docRefListView.setAdapter(userAdapter);

        docRefListView.setOnItemClickListener((parent, v, position, id) -> showPopupDialog(userList.get(position)));

        startListeningToFirestore();
    }

    /**
     * onDestroyView
     *
     * Stops listening to Firestore updates when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopListeningToFirestore();
    }

    /**
     * startListeningToFirestore
     *
     * Initializes a real-time Firestore listener to update the ListView with user profiles.
     */
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

    /**
     * stopListeningToFirestore
     *
     * Removes the Firestore listener.
     */
    private void stopListeningToFirestore() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    /**
     * addUser
     *
     * Adds a new user profile to the ListView when a new document is added in Firestore.
     *
     * @param change The Firestore document change containing the new user data
     */
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

    /**
     * updateUser
     *
     * Updates an existing user profile in the ListView when a Firestore document is modified.
     *
     * @param change The Firestore document change containing updated user data
     */
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

    /**
     * removeUser
     *
     * Removes a user profile from the ListView when a Firestore document is deleted.
     *
     * @param change The Firestore document change containing the removed user data
     */
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

    /**
     * showPopupDialog
     *
     * Displays a dialog showing the full profile picture and options to delete the picture.
     *
     * @param userItem The selected user profile item
     */
    private void showPopupDialog(UserItem userItem) {
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.delete_view_images, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(popupView).create();

        ImageView fullProfileImage = popupView.findViewById(R.id.full_profile_image);
        Button deleteButton = popupView.findViewById(R.id.delete_picture_button);
        Button backButton = popupView.findViewById(R.id.picture_back_button);

        try {
            BitmapHelper bitmapHelper = new BitmapHelper();
            UserProfile userProfile = new UserProfile(userItem.getName(), userItem.getUuid(), userItem.getEncodedPicture());
            Bitmap profilePicture = bitmapHelper.loadProfilePicture(userProfile);
            if (profilePicture != null) {
                fullProfileImage.setImageBitmap(profilePicture);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile picture for UUID: " + userItem.getUuid(), e);
        }

        deleteButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("AllUsers").document(userItem.getUuid())
                    .update("encodedPicture", null)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Profile picture deleted successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        userList.remove(userItem);
                        userAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error deleting profile picture: ", e));
        });

        backButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * UserAdapter
     *
     * Custom ArrayAdapter for displaying user profiles in the ListView.
     */
    private class UserAdapter extends ArrayAdapter<UserItem> {
        public UserAdapter() {
            super(requireContext(), R.layout.item_admin_images, userList);
        }

        @NonNull
        @Override
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
                UserProfile userProfile = new UserProfile(userItem.getName(), userItem.getUuid(), userItem.getEncodedPicture());
                Bitmap profilePicture = bitmapHelper.loadProfilePicture(userProfile);
                if (profilePicture != null) {
                    eventPosterImageView.setImageBitmap(profilePicture);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading profile picture: ", e);
            }

            return convertView;
        }
    }

    /**
     * UserItem
     *
     * Represents a user profile item in the ListView.
     */
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
