package com.example.devpilotai.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.databinding.FragmentStatsBinding;
import com.example.devpilotai.viewmodel.DashboardViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private DashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Use Activity-scoped ViewModel to share data with Dashboard
        viewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        observeViewModel();

        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void observeViewModel() {
        viewModel.getUserStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                String formattedTokens = formatter.format(stats.getTotalTokensUsed());
                
                binding.tvTotalTokens.setText(getString(R.string.total_tokens_format, formattedTokens));
                binding.tvCountSnippets.setText(String.valueOf(stats.getTotalSnippetsSaved()));
                binding.tvCountOCR.setText(String.valueOf(stats.getTotalOcrScans()));
                binding.tvCountNotes.setText(String.valueOf(stats.getTotalNotesCreated()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
