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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.data.AdminLogRepository;
import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.FragmentAdminSettingsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class AdminSettingsFragment extends BaseAdminFragment {

    private FragmentAdminSettingsBinding binding;
    private AdminLogRepository adminLogRepository;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void onAdminVerified(User adminUser) {
        adminLogRepository = new AdminLogRepository();
        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        setupToolbar();
        updateAdminInfo(adminUser);
        setupSettings();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void updateAdminInfo(User user) {
        if (user != null) {
            binding.tvAdminEmail.setText(user.getEmail());
        }
    }

    private void setupSettings() {
        // Theme
        updateThemeText();
        binding.layoutTheme.setOnClickListener(v -> showThemeDialog());

        // Notifications
        boolean notificationsEnabled = prefs.getBoolean("notifications", true);
        binding.switchNotifications.setChecked(notificationsEnabled);
        binding.switchNotifications.setOnCheckedChangeListener((button, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
            adminLogRepository.logAction("SETTINGS_CHANGE", "Notifications", isChecked ? "Enabled" : "Disabled");
        });

        // Change Password
        binding.layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // App Info
        binding.layoutAppInfo.setOnClickListener(v -> showAppInfoDialog());

        // View Activity Logs
        binding.layoutActivityLogs.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_global_adminActivityLogFragment));

        // Logout
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showThemeDialog() {
        String[] themes = {"System Default", "Light", "Dark"};
        int currentTheme = prefs.getInt("theme_mode", 0);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Theme")
                .setSingleChoiceItems(themes, currentTheme, (dialog, which) -> {
                    prefs.edit().putInt("theme_mode", which).apply();
                    updateThemeText();
                    applyTheme(which);
                    adminLogRepository.logAction("SETTINGS_CHANGE", "Theme", themes[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateThemeText() {
        int themeMode = prefs.getInt("theme_mode", 0);
        String[] themes = {"System Default", "Light", "Dark"};
        binding.tvCurrentTheme.setText(themes[themeMode]);
    }

    private void applyTheme(int themeMode) {
        switch (themeMode) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Admin Password")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String password = etNewPassword.getText().toString().trim();
                    if (password.length() >= 6) {
                        adminLogRepository.logAction("PASSWORD_CHANGE", "Admin Account", "Initiated password update");
                        Toast.makeText(getContext(), "Password update initiated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Password too short", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAppInfoDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("App Information")
                .setMessage("DevPilot AI Admin Dashboard\nVersion: 1.0.4\nEnvironment: Production\n\n© 2024 DevPilot AI Team")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout Admin")
                .setMessage("Are you sure you want to end the administrator session?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    adminLogRepository.logAction("ADMIN_LOGOUT", "Admin Session", "User logged out");
                    authViewModel.logOut();
                    Navigation.findNavController(requireView()).navigate(R.id.action_global_loginFragment);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
