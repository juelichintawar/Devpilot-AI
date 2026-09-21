package com.example.devpilotai.ui.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.devpilotai.R;
import com.example.devpilotai.data.model.Challenge;
import com.example.devpilotai.databinding.FragmentChallengeListBinding;
import com.example.devpilotai.viewmodel.ChallengeViewModel;

public class ChallengeListFragment extends Fragment implements ChallengeAdapter.OnChallengeClickListener {

    private FragmentChallengeListBinding binding;
    private ChallengeViewModel viewModel;
    private ChallengeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupFilters();
        observeViewModel();

        binding.fabRandom.setOnClickListener(v -> {
            viewModel.getRandomChallenge().observe(getViewLifecycleOwner(), challenge -> {
                if (challenge != null) {
                    startChallenge(challenge);
                }
            });
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.toolbar.inflateMenu(R.menu.menu_challenges);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_leaderboard) {
                Navigation.findNavController(requireView()).navigate(R.id.action_challengeListFragment_to_leaderboardFragment);
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        adapter = new ChallengeAdapter(this);
        binding.rvChallenges.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChallenges.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.chipGroupDifficulty.setOnCheckedStateChangeListener((group, checkedIds) -> {
            String difficulty = "Easy";
            if (checkedIds.contains(R.id.chipMedium)) difficulty = "Medium";
            else if (checkedIds.contains(R.id.chipHard)) difficulty = "Hard";
            
            updateList(difficulty);
        });
        
        // Initial load
        updateList("Easy");
    }

    private void updateList(String difficulty) {
        viewModel.getChallengesByDifficulty(difficulty).observe(getViewLifecycleOwner(), challenges -> {
            adapter.submitList(challenges);
        });
    }

    private void observeViewModel() {
        viewModel.getCompletedCount().observe(getViewLifecycleOwner(), count -> {
            int completedCount = count != null ? count : 0;
            binding.tvTotalCompleted.setText(getString(R.string.challenges_done, completedCount));
            
            // Assume 20 is the target for now for the progress bar
            int progress = (int) ((completedCount / 20.0) * 100);
            binding.progressOverall.setProgress(Math.min(progress, 100));
        });
    }

    @Override
    public void onChallengeClick(Challenge challenge) {
        startChallenge(challenge);
    }

    private void startChallenge(Challenge challenge) {
        Bundle args = new Bundle();
        args.putInt("challengeId", challenge.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_challengeListFragment_to_challengeSolveFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
