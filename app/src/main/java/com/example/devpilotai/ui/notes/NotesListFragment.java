package com.example.devpilotai.ui.notes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.Folder;
import com.example.devpilotai.data.model.Note;
import com.example.devpilotai.databinding.FragmentNotesListBinding;
import com.example.devpilotai.viewmodel.NoteViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class NotesListFragment extends Fragment implements NotesAdapter.OnNoteClickListener {

    private FragmentNotesListBinding binding;
    private NoteViewModel viewModel;
    private NotesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        setupRecyclerView();
        setupSearch();
        setupFolders();

        binding.fabAddNote.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_notesListFragment_to_noteDetailFragment);
        });

        viewModel.getFilteredNotes().observe(getViewLifecycleOwner(), notes -> {
            adapter.submitList(notes);
        });
    }

    private void setupRecyclerView() {
        adapter = new NotesAdapter(this);
        binding.recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerViewNotes.setAdapter(adapter);
    }

    private void setupSearch() {
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

    private void setupFolders() {
        viewModel.getAllFolders().observe(getViewLifecycleOwner(), folders -> {
            // Keep All Notes and Add Folder chips, then add dynamic ones
            binding.folderChipGroup.removeAllViews();
            
            // All Notes Chip
            Chip allChip = new Chip(requireContext());
            allChip.setText("All Notes");
            allChip.setCheckable(true);
            allChip.setChecked(true);
            allChip.setOnClickListener(v -> viewModel.setSelectedFolder(null));
            binding.folderChipGroup.addView(allChip);

            for (Folder folder : folders) {
                Chip chip = new Chip(requireContext());
                chip.setText(folder.getName());
                chip.setCheckable(true);
                chip.setOnClickListener(v -> viewModel.setSelectedFolder(folder.getId()));
                binding.folderChipGroup.addView(chip);
            }

            // Add Folder Chip
            Chip addFolderChip = new Chip(requireContext());
            addFolderChip.setText("+ Folder");
            addFolderChip.setOnClickListener(v -> showAddFolderDialog());
            binding.folderChipGroup.addView(addFolderChip);
        });
    }

    private void showAddFolderDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Folder Name");
        
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("New Folder")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        viewModel.insertFolder(new Folder(name, System.currentTimeMillis()));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onNoteClick(Note note) {
        Bundle args = new Bundle();
        args.putInt("noteId", note.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_notesListFragment_to_noteDetailFragment, args);
    }

    @Override
    public void onNoteLongClick(Note note) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(note.getTitle())
                .setItems(new CharSequence[]{"Delete", note.isPinned() ? "Unpin" : "Pin"}, (dialog, which) -> {
                    if (which == 0) {
                        viewModel.deleteNote(note);
                    } else {
                        note.setPinned(!note.isPinned());
                        note.setUpdatedAt(System.currentTimeMillis());
                        viewModel.updateNote(note);
                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
