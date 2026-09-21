package com.example.devpilotai.ui.snippet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.Snippet;
import com.example.devpilotai.databinding.FragmentSnippetListBinding;
import com.example.devpilotai.viewmodel.SnippetViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SnippetListFragment extends Fragment implements SnippetAdapter.OnSnippetClickListener {

    private FragmentSnippetListBinding binding;
    private SnippetViewModel viewModel;
    private SnippetAdapter adapter;
    private String selectedCategory = "All";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        binding = FragmentSnippetListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);

        viewModel = new ViewModelProvider(this).get(SnippetViewModel.class);
        setupRecyclerView();

        viewModel.getSnippets().observe(getViewLifecycleOwner(), snippets -> {
            adapter.submitList(snippets);
            binding.tvEmptySnippets.setVisibility(snippets == null || snippets.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), this::populateChips);

        binding.fabAddSnippet.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_snippetListFragment_to_snippetDetailFragment);
        });

        setupSearchView();
    }

    private void setupRecyclerView() {
        adapter = new SnippetAdapter(this);
        binding.recyclerViewSnippets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewSnippets.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void populateChips(List<String> categories) {
        if (categories == null) return;
        
        binding.categoryChips.removeAllViews();
        
        for (String category : categories) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_filter_chip, binding.categoryChips, false);
            chip.setText(category);
            
            if (category.equals(selectedCategory)) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategory = category;
                    viewModel.setCategoryFilter(category);
                }
            });
            binding.categoryChips.addView(chip);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_snippets, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_newest) {
            viewModel.setSortOrder(SnippetViewModel.SortOrder.NEWEST);
            return true;
        } else if (id == R.id.sort_oldest) {
            viewModel.setSortOrder(SnippetViewModel.SortOrder.OLDEST);
            return true;
        } else if (id == R.id.sort_title_az) {
            viewModel.setSortOrder(SnippetViewModel.SortOrder.TITLE_AZ);
            return true;
        } else if (id == R.id.sort_title_za) {
            viewModel.setSortOrder(SnippetViewModel.SortOrder.TITLE_ZA);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSnippetClick(Snippet snippet) {
        Bundle args = new Bundle();
        args.putInt("snippetId", snippet.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_snippetListFragment_to_snippetDetailFragment, args);
    }

    @Override
    public void onFavoriteClick(Snippet snippet) {
        snippet.setFavorite(!snippet.isFavorite());
        viewModel.update(snippet);
    }

    @Override
    public void onDeleteClick(Snippet snippet) {
        viewModel.delete(snippet);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
