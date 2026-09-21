package com.example.devpilotai.ui.challenge;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.example.devpilotai.data.model.Challenge;
import com.example.devpilotai.data.model.ChallengeResult;
import com.example.devpilotai.databinding.FragmentChallengeSolveBinding;
import com.example.devpilotai.viewmodel.AuthViewModel;
import com.example.devpilotai.viewmodel.ChallengeViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class ChallengeSolveFragment extends Fragment {

    private FragmentChallengeSolveBinding binding;
    private ChallengeViewModel viewModel;
    private AuthViewModel authViewModel;
    private CountDownTimer countDownTimer;
    private int challengeId;
    private Challenge currentChallenge;
    private long startTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengeSolveBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        if (getArguments() != null) {
            challengeId = getArguments().getInt("challengeId");
        }

        setupToolbar();
        observeChallenge();
        observeEvaluation();
        setupSubmitButton();
        startTime = System.currentTimeMillis();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void observeChallenge() {
        viewModel.getChallengeById(challengeId).observe(getViewLifecycleOwner(), challenge -> {
            if (challenge != null) {
                currentChallenge = challenge;
                displayChallenge(challenge);
            }
        });
    }

    private void observeEvaluation() {
        viewModel.getIsEvaluating().observe(getViewLifecycleOwner(), isEvaluating -> {
            binding.btnSubmit.setEnabled(!isEvaluating);
            if (isEvaluating) {
                binding.btnSubmit.setText("Evaluating...");
            } else {
                binding.btnSubmit.setText("Submit Solution");
            }
        });

        viewModel.getEvaluationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                handleEvaluationResult(result);
            }
        });
    }

    private void displayChallenge(Challenge challenge) {
        binding.tvTitle.setText(challenge.getTitle());
        binding.tvDescription.setText(challenge.getDescription());
        binding.tvDifficulty.setText(challenge.getDifficulty());
        binding.tvQuestionContent.setText(challenge.getQuestion());
        
        startTimer(challenge.getTimeLimit());
    }

    private void startTimer(int seconds) {
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int remainingSeconds = (int) (millisUntilFinished / 1000);
                int minutes = remainingSeconds / 60;
                int secs = remainingSeconds % 60;
                binding.tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, secs));
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("00:00");
                handleSubmission(false, "Time is up!");
            }
        }.start();
    }

    private void setupSubmitButton() {
        binding.btnSubmit.setOnClickListener(v -> {
            String userAnswer = binding.etSolution.getText().toString().trim();
            if (userAnswer.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your solution", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentChallenge != null) {
                viewModel.evaluateCode(currentChallenge.getTitle(), currentChallenge.getDescription(), userAnswer);
            }
        });
    }

    private void handleEvaluationResult(ChallengeViewModel.EvaluationResult result) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(result.message).append("\n\n");
        
        if (!result.compilerOutput.isEmpty()) {
            messageBuilder.append("Compiler Output:\n").append(result.compilerOutput).append("\n\n");
        }

        try {
            if (result.rawJson != null) {
                JSONObject json = new JSONObject(result.rawJson);
                if (json.has("testCases")) {
                    JSONArray testCases = json.getJSONArray("testCases");
                    messageBuilder.append("Test Cases:\n");
                    for (int i = 0; i < testCases.length(); i++) {
                        JSONObject tc = testCases.getJSONObject(i);
                        String status = tc.getBoolean("passed") ? "✅ PASS" : "❌ FAIL";
                        messageBuilder.append(String.format(Locale.getDefault(), 
                                "Case %d: %s\nInput: %s\nExp: %s | Act: %s\n\n", 
                                i + 1, status, tc.optString("input"), tc.optString("expected"), tc.optString("actual")));
                    }
                }
            }
        } catch (Exception e) {
            // Ignore parsing errors for test cases display
        }

        handleSubmission(result.success, messageBuilder.toString());
    }

    private void handleSubmission(boolean isSuccess, String message) {
        if (countDownTimer != null) countDownTimer.cancel();

        long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
        int score = 0;
        if (isSuccess && currentChallenge != null) {
            switch (currentChallenge.getDifficulty().toLowerCase()) {
                case "easy": score = 10; break;
                case "medium": score = 20; break;
                case "hard": score = 50; break;
            }
        }

        FirebaseUser user = authViewModel.getUserLiveData().getValue();
        String email = user != null ? user.getEmail() : "anonymous";

        ChallengeResult result = new ChallengeResult(challengeId, email, timeTaken, System.currentTimeMillis(), isSuccess, score);
        viewModel.insertResult(result);

        showResultDialog(isSuccess, message);
    }

    private void showResultDialog(boolean success, String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(success ? R.string.challenge_completed : R.string.challenge_failed)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    if (success) {
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        binding = null;
    }
}
