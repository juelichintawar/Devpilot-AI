package com.example.devpilotai.ui.github;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.devpilotai.databinding.FragmentGithubRepoDetailBinding;
import com.example.devpilotai.viewmodel.GitHubViewModel;
import com.google.android.material.tabs.TabLayout;

import io.noties.markwon.Markwon;

public class GitHubRepoDetailFragment extends Fragment {

    private FragmentGithubRepoDetailBinding binding;
    private GitHubViewModel viewModel;
    private GitHubBranchAdapter branchAdapter;
    private GitHubCommitAdapter commitAdapter;
    private GitHubIssueAdapter issueAdapter;
    private Markwon markwon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGithubRepoDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GitHubViewModel.class);
        markwon = Markwon.create(requireContext());

        String owner = GitHubRepoDetailFragmentArgs.fromBundle(getArguments()).getOwner();
        String repoName = GitHubRepoDetailFragmentArgs.fromBundle(getArguments()).getRepoName();

        setupRecyclerViews();
        setupTabs();
        setupActions(owner, repoName);
        observeViewModel(owner, repoName);
        
        // Initial load
        loadReadme(owner, repoName);
    }

    private void setupRecyclerViews() {
        branchAdapter = new GitHubBranchAdapter();
        binding.branchesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.branchesRecyclerView.setAdapter(branchAdapter);

        commitAdapter = new GitHubCommitAdapter();
        binding.commitsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.commitsRecyclerView.setAdapter(commitAdapter);

        issueAdapter = new GitHubIssueAdapter();
        binding.issuesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.issuesRecyclerView.setAdapter(issueAdapter);
    }

    private void setupTabs() {
        binding.detailTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabVisibility(tab.getPosition());
                String owner = GitHubRepoDetailFragmentArgs.fromBundle(getArguments()).getOwner();
                String repoName = GitHubRepoDetailFragmentArgs.fromBundle(getArguments()).getRepoName();

                switch (tab.getPosition()) {
                    case 0: loadReadme(owner, repoName); break;
                    case 1: loadCommits(owner, repoName); break;
                    case 2: loadBranches(owner, repoName); break;
                    case 3: loadIssues(owner, repoName); break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupActions(String owner, String repoName) {
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnAnalyzeRepo.setOnClickListener(v -> viewModel.analyzeRepository(owner, repoName));
    }

    private void updateTabVisibility(int position) {
        binding.readmeContainer.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        binding.commitsRecyclerView.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        binding.branchesRecyclerView.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
        binding.issuesRecyclerView.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
    }

    private void observeViewModel(String owner, String repoName) {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.detailProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnAnalyzeRepo.setEnabled(!isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getRepositoryDetails(owner, repoName).observe(getViewLifecycleOwner(), repo -> {
            if (repo != null) {
                binding.repoNameDetail.setText(repo.getName());
                binding.repoFullNameDetail.setText(repo.getFullName());
                binding.repoDescriptionDetail.setText(repo.getDescription() != null ? repo.getDescription() : "No description");
                binding.repoStarsDetail.setText(String.valueOf(repo.getStargazersCount()));
                binding.repoForksDetail.setText(String.valueOf(repo.getForksCount()));
            }
        });

        viewModel.getAiAnalysisResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                binding.aiReviewCard.setVisibility(View.VISIBLE);
                markwon.setMarkdown(binding.aiReviewTextView, result);
                
                // Extract score if present in the text (simple heuristic)
                if (result.contains("Score:")) {
                    try {
                        String scorePart = result.split("Score:")[1].trim().split("\n")[0].replaceAll("[^0-9]", "");
                        if (!scorePart.isEmpty()) {
                            binding.qualityScoreChip.setText("Score: " + scorePart);
                        }
                    } catch (Exception ignored) {}
                }
            } else {
                binding.aiReviewCard.setVisibility(View.GONE);
            }
        });
    }

    private void loadReadme(String owner, String repoName) {
        viewModel.getReadme(owner, repoName).observe(getViewLifecycleOwner(), readme -> {
            if (readme != null) {
                if (readme.trim().startsWith("<")) {
                    binding.readmeTextView.setText(Html.fromHtml(readme, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    markwon.setMarkdown(binding.readmeTextView, readme);
                }
            } else {
                binding.readmeTextView.setText("No README available");
            }
        });
    }

    private void loadCommits(String owner, String repoName) {
        viewModel.getCommits(owner, repoName).observe(getViewLifecycleOwner(), commits -> {
            commitAdapter.setCommits(commits);
        });
    }

    private void loadBranches(String owner, String repoName) {
        viewModel.getBranches(owner, repoName).observe(getViewLifecycleOwner(), branches -> {
            branchAdapter.setBranches(branches);
        });
    }

    private void loadIssues(String owner, String repoName) {
        viewModel.getIssues(owner, repoName).observe(getViewLifecycleOwner(), issues -> {
            issueAdapter.setIssues(issues);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
