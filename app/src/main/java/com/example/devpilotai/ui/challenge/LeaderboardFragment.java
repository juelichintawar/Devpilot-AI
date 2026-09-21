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

import com.bumptech.glide.Glide;
import com.example.devpilotai.R;
import com.example.devpilotai.databinding.FragmentLeaderboardBinding;
import com.example.devpilotai.viewmodel.ChallengeViewModel;
import com.example.devpilotai.data.model.ChallengeResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private ChallengeViewModel viewModel;
    private LeaderboardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter();
        binding.rvLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvLeaderboard.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getAllResults().observe(getViewLifecycleOwner(), results -> {
            if (results != null && !results.isEmpty()) {
                // Group by user and sum scores
                Map<String, ChallengeResult> userScores = new HashMap<>();
                for (ChallengeResult r : results) {
                    if (r.isSuccess()) {
                        if (userScores.containsKey(r.getUserEmail())) {
                            ChallengeResult existing = userScores.get(r.getUserEmail());
                            // Create a dummy result for aggregation
                            userScores.put(r.getUserEmail(), new ChallengeResult(
                                    0, r.getUserEmail(), 0, 0, true, 
                                    existing.getScore() + r.getScore()));
                        } else {
                            userScores.put(r.getUserEmail(), r);
                        }
                    }
                }

                List<ChallengeResult> sortedResults = new ArrayList<>(userScores.values());
                Collections.sort(sortedResults, (a, b) -> Integer.compare(b.getScore(), a.getScore()));

                updatePodium(sortedResults);
                
                if (sortedResults.size() > 3) {
                    adapter.setResults(sortedResults.subList(3, sortedResults.size()));
                } else {
                    adapter.setResults(new ArrayList<>());
                }
            }
        });
    }

    private void updatePodium(List<ChallengeResult> sortedResults) {
        if (sortedResults.size() >= 1) {
            binding.podium1Name.setText(sortedResults.get(0).getUserEmail().split("@")[0]);
            binding.podium1Score.setText(sortedResults.get(0).getScore() + " pts");
        }
        if (sortedResults.size() >= 2) {
            binding.podium2Name.setText(sortedResults.get(1).getUserEmail().split("@")[0]);
            binding.podium2Score.setText(sortedResults.get(1).getScore() + " pts");
        }
        if (sortedResults.size() >= 3) {
            binding.podium3Name.setText(sortedResults.get(2).getUserEmail().split("@")[0]);
            binding.podium3Score.setText(sortedResults.get(2).getScore() + " pts");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
