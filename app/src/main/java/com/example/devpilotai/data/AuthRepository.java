package com.example.devpilotai.data;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> userLiveData;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        // Set initial value immediately on the main thread to avoid navigation loops
        userLiveData.setValue(firebaseAuth.getCurrentUser());
    }

    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        userLiveData.postValue(user);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Login failed");
                    }
                });
    }

    public void register(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        userLiveData.postValue(user);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Registration failed");
                    }
                });
    }

    public void firebaseSignInWithGoogle(AuthCredential credential, AuthCallback callback) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        userLiveData.postValue(user);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Google sign-in failed");
                    }
                });
    }

    public void updateProfile(String displayName, String photoUrl, AuthCallback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName);
            
            if (photoUrl != null && !photoUrl.isEmpty()) {
                builder.setPhotoUri(Uri.parse(photoUrl));
            }

            UserProfileChangeRequest profileUpdates = builder.build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Reload user to get updated data
                            user.reload().addOnCompleteListener(reloadTask -> {
                                userLiveData.postValue(firebaseAuth.getCurrentUser());
                                callback.onSuccess(firebaseAuth.getCurrentUser());
                            });
                        } else {
                            callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Profile update failed");
                        }
                    });
        }
    }

    public void resetPassword(String email, AuthCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Password reset failed");
                    }
                });
    }

    public void logOut() {
        firebaseAuth.signOut();
        userLiveData.postValue(null);
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }
}