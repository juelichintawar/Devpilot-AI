package com.example.devpilotai.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.devpilotai.databinding.FragmentSearchBinding;
import com.example.devpilotai.viewmodel.GlobalSearchViewModel;
import com.google.android.material.transition.MaterialContainerTransform;

/**
 * Fragment for global search across the app.
 * Employs MaterialContainerTransform for shared element transitions.
 */
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private GlobalSearchViewModel viewModel;
    private SearchAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Material Motion: Shared Element Transition
        setSharedElementEnterTransition(new MaterialContainerTransform());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GlobalSearchViewModel.class);

        setupRecyclerView();
        setupSearchInput();
        observeViewModel();

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnClear.setOnClickListener(v -> binding.etSearch.setText(""));
    }

    private void setupRecyclerView() {
        adapter = new SearchAdapter(result -> {
            Bundle bundle = new Bundle();
            switch (result.getType()) {
                case SNIPPET:
                    bundle.putInt("snippetId", Integer.parseInt(result.getId()));
                    Navigation.findNavController(requireView()).navigate(com.example.devpilotai.R.id.snippetDetailFragment, bundle);
                    break;
                case NOTE:
                    bundle.putInt("noteId", Integer.parseInt(result.getId()));
                    Navigation.findNavController(requireView()).navigate(com.example.devpilotai.R.id.noteDetailFragment, bundle);
                    break;
                case CHAT:
                    Navigation.findNavController(requireView()).navigate(com.example.devpilotai.R.id.chatFragment);
                    break;
                case REPOSITORY:
                    // Navigate to repo details could be implemented here
                    break;
            }
        });
        binding.rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvResults.setAdapter(adapter);
    }

    private void setupSearchInput() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                viewModel.setQuery(query);
                binding.btnClear.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        binding.etSearch.requestFocus();
    }

    private void observeViewModel() {
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            // Update adapter with new list and query for highlighting
            adapter.setQuery(viewModel.getQuery());
            adapter.submitList(results);
            
            // Handle empty state visibility if necessary
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
