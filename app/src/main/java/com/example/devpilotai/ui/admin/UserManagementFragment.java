package com.example.devpilotai.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.FragmentUserManagementBinding;
import com.example.devpilotai.viewmodel.UserManagementViewModel;

public class UserManagementFragment extends BaseAdminFragment implements UserAdapter.OnUserActionListener {

    private FragmentUserManagementBinding binding;
    private UserManagementViewModel viewModel;
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void onAdminVerified(User adminUser) {
        viewModel = new ViewModelProvider(this).get(UserManagementViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupSearch();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(this);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (viewModel != null) {
                    viewModel.filterUsers(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getFilteredUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.setUsers(users);
            binding.tvEmpty.setVisibility(users == null || users.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onToggleStatus(User user) {
        String newStatus = "Active".equalsIgnoreCase(user.getStatus()) ? "Deactivated" : "Active";
        new AlertDialog.Builder(requireContext())
                .setTitle("Update Status")
                .setMessage("Change status of " + user.getEmail() + " to " + newStatus + "?")
                .setPositiveButton("Update", (dialog, which) -> viewModel.updateUserStatus(user, newStatus))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDeleteUser(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user " + user.getEmail() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onUserClick(User user) {
        // Implementation for showing user details summary
        StringBuilder summary = new StringBuilder();
        summary.append("Email: ").append(user.getEmail()).append("\n");
        summary.append("Status: ").append(user.getStatus()).append("\n");
        summary.append("Role: ").append(user.getRole()).append("\n");
        summary.append("\nUser activity summary would be displayed here.");

        new AlertDialog.Builder(requireContext())
                .setTitle("User Details")
                .setMessage(summary.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
