package com.example.devpilotai.ui.github;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.GitHubRepo;
import com.example.devpilotai.databinding.FragmentGithubRepoListBinding;
import com.example.devpilotai.viewmodel.GitHubViewModel;

import java.util.List;

public class GitHubRepoListFragment extends Fragment implements GitHubRepoAdapter.OnRepoClickListener {

    private FragmentGithubRepoListBinding binding;
    private GitHubViewModel viewModel;
    private GitHubRepoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGithubRepoListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GitHubViewModel.class);

        setupRecyclerView();
        setupSearch();
        observeViewModel();

        loadRepositories();
    }

    private void setupRecyclerView() {
        adapter = new GitHubRepoAdapter(this);
        binding.repoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.repoRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    loadRepositories();
                }
                return true;
            }
            return false;
        });

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    loadRepositories();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void loadRepositories() {
        viewModel.getRepositories().observe(getViewLifecycleOwner(), this::handleReposUpdate);
    }

    private void performSearch(String query) {
        viewModel.searchRepositories(query).observe(getViewLifecycleOwner(), this::handleReposUpdate);
    }

    private void handleReposUpdate(List<GitHubRepo> repos) {
        viewModel.setLoading(false);
        adapter.setRepos(repos);
        binding.emptyStateText.setVisibility(repos == null || repos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRepoClick(GitHubRepo repo) {
        Bundle args = new Bundle();
        args.putString("owner", repo.getOwner().getLogin());
        args.putString("repoName", repo.getName());
        // Use the action ID defined for GitHubDashboardFragment in nav_graph.xml, 
        // as GitHubRepoListFragment is hosted within it.
        Navigation.findNavController(requireView()).navigate(R.id.action_githubDashboardFragment_to_githubRepoDetailFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
