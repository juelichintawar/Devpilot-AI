package com.example.devpilotai.ui.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.data.AdminLogRepository;
import com.example.devpilotai.data.UserRepository;
import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.FragmentAdminLoginBinding;
import com.example.devpilotai.utils.NetworkUtils;
import com.example.devpilotai.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginFragment extends Fragment {

    private FragmentAdminLoginBinding binding;
    private AuthViewModel authViewModel;
    private AdminLogRepository adminLogRepository;
    private UserRepository userRepository;

    // Hardcoded Admin Credentials for Emergency/Offline Access
    private static final String SUPER_ADMIN_EMAIL = "admin@devpilot.ai";
    private static final String SUPER_ADMIN_PASS = "admin123";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        adminLogRepository = new AdminLogRepository();
        userRepository = new UserRepository();

        binding.btnAdminLogin.setOnClickListener(v -> {
            String email = binding.etAdminEmail.getText().toString().trim();
            String password = binding.etAdminPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please enter admin credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            // OFFLINE BYPASS: Allow access with hardcoded credentials
            if (SUPER_ADMIN_EMAIL.equalsIgnoreCase(email) && SUPER_ADMIN_PASS.equals(password)) {
                setLocalAdminStatus(true);
                User bypassUser = new User("admin_bypass_id", email, "Super Admin", "Active", "Admin", System.currentTimeMillis());
                proceedToDashboard(bypassUser);
                return;
            }

            // Normal Connectivity Check for other accounts
            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                Toast.makeText(getContext(), "Offline: Use Super Admin credentials or connect to internet.", Toast.LENGTH_LONG).show();
                return;
            }

            authViewModel.login(email, password);
        });

        binding.tvBackToUser.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        observeViewModel();
    }

    private void setLocalAdminStatus(boolean isActive) {
        SharedPreferences prefs = requireContext().getSharedPreferences("admin_prefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("is_local_admin", isActive).apply();
    }

    private void observeViewModel() {
        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                // If we logged in via Firebase normally, ensure local bypass flag is off
                setLocalAdminStatus(false);
                checkAdminRole(firebaseUser);
            }
        });

        authViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Auth Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            binding.adminProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnAdminLogin.setEnabled(!isLoading);
        });
    }

    private void checkAdminRole(FirebaseUser firebaseUser) {
        if (!isAdded()) return;
        
        binding.adminProgressBar.setVisibility(View.VISIBLE);

        // Bypass check for super admin email during normal auth flow
        if (SUPER_ADMIN_EMAIL.equalsIgnoreCase(firebaseUser.getEmail())) {
            proceedToDashboard(new User(firebaseUser.getUid(), firebaseUser.getEmail(), "Super Admin", "Active", "Admin", System.currentTimeMillis()));
            return;
        }

        userRepository.getUserFromServer(firebaseUser.getUid(), new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (isAdded()) {
                    binding.adminProgressBar.setVisibility(View.GONE);
                    if (user != null && "Admin".equalsIgnoreCase(user.getRole())) {
                        proceedToDashboard(user);
                    } else {
                        authViewModel.logOut();
                        Toast.makeText(getContext(), "Access Denied: Admin privileges required.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                if (isAdded()) {
                    binding.adminProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Verification Failed: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void proceedToDashboard(User user) {
        adminLogRepository.logAction("ADMIN_LOGIN", user.getEmail(), "Logged into admin dashboard");
        binding.adminProgressBar.setVisibility(View.GONE);
        Navigation.findNavController(requireView()).navigate(R.id.action_adminLoginFragment_to_adminDashboardFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
