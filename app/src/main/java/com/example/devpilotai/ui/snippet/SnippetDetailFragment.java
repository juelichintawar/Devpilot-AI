package com.example.devpilotai.ui.snippet;

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

import com.example.devpilotai.databinding.FragmentSnippetDetailBinding;
import com.example.devpilotai.data.model.Snippet;
import com.example.devpilotai.viewmodel.SnippetViewModel;

public class SnippetDetailFragment extends Fragment {

    private FragmentSnippetDetailBinding binding;
    private SnippetViewModel viewModel;
    private int snippetId = -1;
    private Snippet currentSnippet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSnippetDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SnippetViewModel.class);

        if (getArguments() != null) {
            snippetId = getArguments().getInt("snippetId", -1);
        }

        if (snippetId != -1) {
            binding.toolbarDetail.setTitle("Edit Snippet");
            // Efficiently fetch only the required snippet
            viewModel.getSnippetById(snippetId).observe(getViewLifecycleOwner(), snippet -> {
                if (snippet != null) {
                    currentSnippet = snippet;
                    binding.editSnippetTitle.setText(snippet.getTitle());
                    binding.editSnippetContent.setText(snippet.getContent());
                    binding.editCategory.setText(snippet.getCategory());
                    binding.editTags.setText(snippet.getTags());
                }
            });
        } else {
            binding.toolbarDetail.setTitle("New Snippet");
        }

        binding.btnSaveSnippet.setOnClickListener(v -> saveSnippet());
        binding.toolbarDetail.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void saveSnippet() {
        String title = binding.editSnippetTitle.getText().toString().trim();
        String content = binding.editSnippetContent.getText().toString().trim();
        String category = binding.editCategory.getText().toString().trim();
        String tags = binding.editTags.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        if (snippetId == -1) {
            Snippet newSnippet = new Snippet(title, content, category, tags, false, System.currentTimeMillis());
            viewModel.insert(newSnippet);
            Toast.makeText(requireContext(), "Snippet saved", Toast.LENGTH_SHORT).show();
        } else if (currentSnippet != null) {
            currentSnippet.setTitle(title);
            currentSnippet.setContent(content);
            currentSnippet.setCategory(category);
            currentSnippet.setTags(tags);
            viewModel.update(currentSnippet);
            Toast.makeText(requireContext(), "Snippet updated", Toast.LENGTH_SHORT).show();
        }

        Navigation.findNavController(requireView()).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
