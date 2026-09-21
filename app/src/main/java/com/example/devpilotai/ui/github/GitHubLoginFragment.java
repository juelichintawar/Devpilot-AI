package com.example.devpilotai.ui.github;

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
import com.example.devpilotai.databinding.FragmentGithubLoginBinding;
import com.example.devpilotai.viewmodel.GitHubViewModel;

public class GitHubLoginFragment extends Fragment {

    private FragmentGithubLoginBinding binding;
    private GitHubViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGithubLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GitHubViewModel.class);

        if (viewModel.isLoggedIn()) {
            navigateToDashboard();
            return;
        }

        binding.loginButton.setOnClickListener(v -> {
            String token = binding.tokenEditText.getText().toString().trim();
            if (token.isEmpty()) {
                binding.tokenInputLayout.setError("Token is required");
                return;
            }
            binding.tokenInputLayout.setError(null);
            performLogin(token);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.loginButton.setEnabled(!isLoading);
        });
    }

    private void performLogin(String token) {
        viewModel.login(token).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Toast.makeText(requireContext(), "Connected as " + user.getLogin(), Toast.LENGTH_SHORT).show();
                navigateToDashboard();
            } else {
                Toast.makeText(requireContext(), "Invalid token or connection error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToDashboard() {
        Navigation.findNavController(requireView()).navigate(R.id.action_githubLoginFragment_to_githubDashboardFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
