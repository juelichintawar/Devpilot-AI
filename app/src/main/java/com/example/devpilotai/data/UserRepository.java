package com.example.devpilotai.data;

import com.example.devpilotai.data.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final FirebaseFirestore db;
    private final CollectionReference usersRef;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        
        try {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
        } catch (IllegalStateException e) {
            // Settings already initialized
        }
        
        db.enableNetwork();
        usersRef = db.collection("users");
    }

    /**
     * Fetches user data using a Snapshot Listener.
     * This is the most robust way to handle the transition from offline to online.
     * It will wait for the connection to establish rather than failing immediately.
     */
    public void getUser(String uid, UserCallback callback) {
        usersRef.document(uid).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                String msg = e.getMessage().toLowerCase();
                // If offline, we just wait. The listener will trigger again when online.
                if (!msg.contains("offline") && !msg.contains("unavailable")) {
                    callback.onFailure(e.getMessage());
                }
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                parseAndReturnUser(snapshot, uid, callback);
            } else if (snapshot != null && !snapshot.getMetadata().isFromCache()) {
                // If it definitely doesn't exist on server
                callback.onFailure("User profile not found.");
            }
        });
    }

    /**
     * Recommended for login verification.
     */
    public void getUserFromServer(String uid, UserCallback callback) {
        db.enableNetwork();
        getUser(uid, callback);
    }

    private void parseAndReturnUser(DocumentSnapshot document, String uid, UserCallback callback) {
        try {
            User user = document.toObject(User.class);
            if (user != null) {
                user.setUid(uid);
                callback.onSuccess(user);
            } else {
                callback.onFailure("Failed to parse user data.");
            }
        } catch (Exception e) {
            callback.onFailure("Data parsing error.");
        }
    }

    public void getAllUsers(UsersCallback callback) {
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    user.setUid(document.getId());
                    userList.add(user);
                }
                callback.onSuccess(userList);
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Failed to fetch users");
            }
        });
    }

    public void updateUserStatus(String uid, String status, ActionCallback callback) {
        usersRef.document(uid).update("status", status)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateGitHubConnectionStatus(String uid, boolean isConnected) {
        usersRef.document(uid).update("gitHubConnected", isConnected);
    }

    public void deleteUser(String uid, ActionCallback callback) {
        usersRef.document(uid).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onFailure(String message);
    }

    public interface ActionCallback {
        void onSuccess();
        void onFailure(String message);
    }
}
