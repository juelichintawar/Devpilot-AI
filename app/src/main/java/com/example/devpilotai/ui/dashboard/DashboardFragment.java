package com.example.devpilotai.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.devpilotai.R;
import com.example.devpilotai.databinding.FragmentDashboardBinding;
import com.example.devpilotai.viewmodel.AuthViewModel;
import com.example.devpilotai.viewmodel.DashboardViewModel;
import com.google.android.material.transition.MaterialFadeThrough;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Main dashboard fragment of the application.
 * Employs a production-ready MVVM architecture, Material Motion, and optimized list rendering.
 */
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private AuthViewModel authViewModel;
    private DashboardViewModel dashboardViewModel;
    private QuickActionAdapter quickActionAdapter;
    private RecentActivityAdapter recentActivityAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Material Motion: Fade through transition for top-level navigation
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        // Shared ViewModel at Activity level to persist data across fragments
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        setupAdapters();
        setupNavigation();
        setupSearch();
        observeViewModels();

        // Load data through ViewModel
        dashboardViewModel.loadDashboardData();
    }

    private void setupAdapters() {
        quickActionAdapter = new QuickActionAdapter(action -> {
            NavController navController = Navigation.findNavController(requireView());
            String title = action.getTitle();
            if (title.equals(getString(R.string.ai_chat))) {
                navController.navigate(R.id.action_dashboardFragment_to_chatFragment);
            } else if (title.equals(getString(R.string.ocr_scanner))) {
                navController.navigate(R.id.action_dashboardFragment_to_ocrFragment);
            } else if (title.equals(getString(R.string.snippets))) {
                navController.navigate(R.id.action_dashboardFragment_to_snippetListFragment);
            } else if (title.equals(getString(R.string.notes))) {
                navController.navigate(R.id.action_dashboardFragment_to_notesListFragment);
            } else if (title.equals(getString(R.string.github))) {
                navController.navigate(R.id.action_dashboardFragment_to_githubLoginFragment);
            } else if (title.equals(getString(R.string.challenges))) {
                navController.navigate(R.id.action_dashboardFragment_to_challengeListFragment);
            } else if (title.equals(getString(R.string.profile))) {
                navController.navigate(R.id.action_dashboardFragment_to_profileFragment);
            } else if (title.equals(getString(R.string.history))) {
                navController.navigate(R.id.action_dashboardFragment_to_historyFragment);
            }
        });

        binding.rvQuickActions.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.rvQuickActions.setAdapter(quickActionAdapter);

        recentActivityAdapter = new RecentActivityAdapter();
        binding.rvRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecentActivity.setAdapter(recentActivityAdapter);
    }

    private void observeViewModels() {
        // Observe loading state
        dashboardViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::showLoading);

        // Observe Dashboard data
        dashboardViewModel.getQuickActions().observe(getViewLifecycleOwner(), actions -> {
            quickActionAdapter.submitList(actions);
            setupHeader(); // Refresh header info when data loads
        });

        dashboardViewModel.getRecentActivities().observe(getViewLifecycleOwner(), activities -> {
            recentActivityAdapter.submitList(activities);
            binding.emptyState.setVisibility(activities.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // Observe Real-time Stats
        dashboardViewModel.getUserStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                String formattedTokens = formatter.format(stats.getTotalTokensUsed());
                binding.tvStatsTokens.setText(getString(R.string.tokens_used_format, formattedTokens));
                binding.tvStatsSnippets.setText(String.valueOf(stats.getTotalSnippetsSaved()));
                binding.tvStatsOCR.setText(String.valueOf(stats.getTotalOcrScans()));
            }
        });

        // Observe User state
        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null && binding != null) {
                String name = firebaseUser.getDisplayName();
                binding.tvUserName.setText(name != null && !name.isEmpty() ? name : "Developer");
                
                if (firebaseUser.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(firebaseUser.getPhotoUrl())
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .circleCrop()
                            .into(binding.ivProfile);
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.skeletonLayout.getRoot().setVisibility(View.VISIBLE);
            binding.mainContent.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.GONE);
        } else {
            binding.skeletonLayout.getRoot().setVisibility(View.GONE);
            binding.mainContent.setVisibility(View.VISIBLE);
            
            // Content Entrance Animation
            binding.mainContent.setAlpha(0f);
            binding.mainContent.setTranslationY(40f);
            binding.mainContent.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .start();
        }
    }

    private void setupHeader() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault());
        binding.tvDate.setText(dateFormat.format(new Date()));
        binding.tvGreeting.setText(getGreeting());
    }

    private String getGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour < 12) return getString(R.string.good_morning);
        if (hour < 17) return "Good Afternoon,";
        return "Good Evening,";
    }

    private void setupNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            NavController navController = Navigation.findNavController(requireView());
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_stats) {
                navController.navigate(R.id.action_dashboardFragment_to_statsFragment);
                return true;
            }
            if (id == R.id.nav_chat) {
                navController.navigate(R.id.action_dashboardFragment_to_chatFragment);
                return true;
            }
            if (id == R.id.nav_profile) {
                navController.navigate(R.id.action_dashboardFragment_to_profileFragment);
                return true;
            }
            return false;
        });

        binding.fab.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_notesListFragment));
        binding.ivProfile.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_profileFragment));
        
        // Navigation for Stats card and History "View All"
        binding.statsCard.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_statsFragment));
        binding.tvViewAll.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_historyFragment));
    }

    private void setupSearch() {
        binding.searchCard.setOnClickListener(v -> {
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(binding.searchCard, "search_bar_transition")
                    .build();

            Navigation.findNavController(v).navigate(
                    R.id.action_dashboardFragment_to_searchFragment,
                    null,
                    null,
                    extras);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
