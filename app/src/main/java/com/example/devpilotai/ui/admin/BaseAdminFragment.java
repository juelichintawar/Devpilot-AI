package com.example.devpilotai.ui.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.data.UserRepository;
import com.example.devpilotai.data.model.User;
import com.example.devpilotai.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * Base class for all Admin fragments to ensure consistent security checks.
 * Redirects to login if the user is not an authenticated Administrator.
 */
public abstract class BaseAdminFragment extends Fragment {

    protected AuthViewModel authViewModel;
    protected UserRepository userRepository;
    
    // Unified Admin Bypass Credentials
    private static final String SUPER_ADMIN_EMAIL = "admin@admin.com";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        userRepository = new UserRepository();

        secureScreen();
    }

    private void secureScreen() {
        // 1. Check if we have a local bypass session active
        SharedPreferences adminPrefs = requireContext().getSharedPreferences("admin_prefs", Context.MODE_PRIVATE);
        boolean isLocalAdmin = adminPrefs.getBoolean("is_local_admin", false);
        
        FirebaseUser firebaseUser = authViewModel.getUserLiveData().getValue();

        // 2. Allow access if local bypass is active OR if it's the specific super admin email
        if (isLocalAdmin || (firebaseUser != null && SUPER_ADMIN_EMAIL.equalsIgnoreCase(firebaseUser.getEmail()))) {
            String email = firebaseUser != null ? firebaseUser.getEmail() : SUPER_ADMIN_EMAIL;
            String uid = firebaseUser != null ? firebaseUser.getUid() : "admin_bypass_id";
            
            onAdminVerified(new User(uid, email, "Super Admin", "Active", "Admin", System.currentTimeMillis()));
            return;
        }

        // 3. Fallback to normal verification if no bypass is active
        if (firebaseUser == null) {
            handleUnauthorized("Session expired. Please login again.");
            return;
        }

        userRepository.getUser(firebaseUser.getUid(), new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (!isAdded()) return;
                if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
                    authViewModel.logOut();
                    handleUnauthorized("Access Denied: Admin privileges required.");
                } else {
                    onAdminVerified(user);
                }
            }

            @Override
            public void onFailure(String message) {
                if (!isAdded()) return;
                // Offline fallback for already logged-in users
                if (message != null && message.toLowerCase().contains("offline")) {
                    // If we previously had a valid user object in cache, Firestore's getUser
                    // should have succeeded via parseAndReturnUser. If it failed with "offline",
                    // it means even the cache is missing. 
                    handleUnauthorized("Verification failed: Device is offline and no profile cached.");
                } else {
                    handleUnauthorized("Security verification failed: " + message);
                }
            }
        });
    }

    private void handleUnauthorized(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            Navigation.findNavController(requireView()).navigate(R.id.action_global_loginFragment);
        }
    }

    /**
     * Called when the administrator role has been verified.
     */
    protected abstract void onAdminVerified(User adminUser);
}
