package com.example.devpilotai.ui.dashboard;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.devpilotai.R;
import com.example.devpilotai.databinding.DialogEditProfileBinding;
import com.example.devpilotai.databinding.FragmentProfileBinding;
import com.example.devpilotai.viewmodel.AuthViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        setupToolbar();
        observeUser();
        setupSettings();
        setupAccountActions();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void observeUser() {
        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUserName.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() 
                        ? user.getDisplayName() : "Developer");
                binding.tvEmail.setText(user.getEmail());
                
                if (user.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(binding.ivProfileLarge);
                }
            }
        });

        authViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSettings() {
        // Theme Selection
        updateThemeText();
        binding.layoutTheme.setOnClickListener(v -> showThemeDialog());

        // Notifications
        boolean notificationsEnabled = prefs.getBoolean("notifications", true);
        binding.switchNotifications.setChecked(notificationsEnabled);
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(getContext(), "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        // Language
        binding.layoutLanguage.setOnClickListener(v -> showLanguageDialog());
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
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    private void updateThemeText() {
        int themeMode = prefs.getInt("theme_mode", 0);
        String[] themes = {"System Default", "Light", "Dark"};
        binding.tvCurrentTheme.setText(themes[themeMode]);
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Spanish", "French", "German", "Hindi"};
        int checkedItem = 0; // Default to English
        
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Language")
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    binding.tvCurrentLanguage.setText(languages[which]);
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Language changed to " + languages[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void setupAccountActions() {
        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        binding.layoutSync.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Syncing with Firebase...", Toast.LENGTH_SHORT).show();
        });

        binding.layoutLogout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        authViewModel.logOut();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showEditProfileDialog() {
        FirebaseUser user = authViewModel.getUserLiveData().getValue();
        DialogEditProfileBinding dialogBinding = DialogEditProfileBinding.inflate(getLayoutInflater());
        
        if (user != null) {
            dialogBinding.etName.setText(user.getDisplayName());
            if (user.getPhotoUrl() != null) {
                dialogBinding.etPhotoUrl.setText(user.getPhotoUrl().toString());
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = dialogBinding.etName.getText().toString().trim();
                    String photoUrl = dialogBinding.etPhotoUrl.getText().toString().trim();
                    
                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    authViewModel.updateProfile(name, photoUrl);
                    Toast.makeText(getContext(), "Updating profile...", Toast.LENGTH_SHORT).show();
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
