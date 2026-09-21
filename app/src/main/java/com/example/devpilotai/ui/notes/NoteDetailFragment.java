package com.example.devpilotai.ui.notes;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.Folder;
import com.example.devpilotai.data.model.Note;
import com.example.devpilotai.databinding.FragmentNoteDetailBinding;
import com.example.devpilotai.viewmodel.NoteViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.noties.markwon.Markwon;

public class NoteDetailFragment extends Fragment {

    private FragmentNoteDetailBinding binding;
    private NoteViewModel viewModel;
    private Note currentNote;
    private int noteId = -1;
    private Markwon markwon;
    private boolean isPreviewMode = false;
    
    private final Handler autosaveHandler = new Handler(Looper.getMainLooper());
    private Runnable autosaveRunnable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            noteId = getArguments().getInt("noteId", -1);
        }
        markwon = Markwon.create(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        setupToolbar();
        setupAutosave();
        setupFormattingButtons();
        setupFolderSelection();

        if (noteId != -1) {
            viewModel.getNoteById(noteId).observe(getViewLifecycleOwner(), note -> {
                if (note != null && currentNote == null) {
                    currentNote = note;
                    displayNote();
                }
            });
        } else {
            currentNote = new Note("", "", System.currentTimeMillis(), System.currentTimeMillis(), false, null);
            displayNote();
        }

        binding.fabSave.setOnClickListener(v -> saveNote(true));
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.toolbar.inflateMenu(R.menu.menu_note_detail);
        
        // Add Preview Toggle to menu dynamically if not in XML
        Menu menu = binding.toolbar.getMenu();
        MenuItem previewItem = menu.add(0, 999, 0, "Preview");
        previewItem.setIcon(android.R.drawable.ic_menu_view);
        previewItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_pin) {
                togglePin();
                return true;
            } else if (id == R.id.action_share) {
                shareNote();
                return true;
            } else if (id == R.id.action_export_pdf) {
                exportToPdf();
                return true;
            } else if (id == R.id.action_delete) {
                deleteNote();
                return true;
            } else if (id == 999) {
                togglePreviewMode();
                return true;
            }
            return false;
        });
    }

    private void togglePreviewMode() {
        isPreviewMode = !isPreviewMode;
        if (isPreviewMode) {
            binding.editTextContent.setVisibility(View.GONE);
            binding.textViewPreview.setVisibility(View.VISIBLE);
            binding.formattingToolbar.setVisibility(View.GONE);
            markwon.setMarkdown(binding.textViewPreview, binding.editTextContent.getText().toString());
        } else {
            binding.editTextContent.setVisibility(View.VISIBLE);
            binding.textViewPreview.setVisibility(View.GONE);
            binding.formattingToolbar.setVisibility(View.VISIBLE);
        }
    }

    private void setupAutosave() {
        autosaveRunnable = () -> saveNote(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                autosaveHandler.removeCallbacks(autosaveRunnable);
                autosaveHandler.postDelayed(autosaveRunnable, 3000); // 3 seconds delay
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.editTextTitle.addTextChangedListener(textWatcher);
        binding.editTextContent.addTextChangedListener(textWatcher);
    }

    private void setupFormattingButtons() {
        binding.btnBold.setOnClickListener(v -> insertFormatting("**", "**"));
        binding.btnItalic.setOnClickListener(v -> insertFormatting("_", "_"));
        binding.btnList.setOnClickListener(v -> insertFormatting("\n- ", ""));
    }

    private void setupFolderSelection() {
        binding.btnFolder.setOnClickListener(v -> {
            viewModel.getAllFolders().observe(getViewLifecycleOwner(), folders -> {
                if (folders == null || folders.isEmpty()) {
                    Toast.makeText(requireContext(), "No folders found. Create one in the notes list.", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String[] folderNames = new String[folders.size() + 1];
                folderNames[0] = "None (Uncategorized)";
                for (int i = 0; i < folders.size(); i++) {
                    folderNames[i + 1] = folders.get(i).getName();
                }

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Move to Folder")
                        .setItems(folderNames, (dialog, which) -> {
                            if (which == 0) {
                                currentNote.setFolderId(null);
                            } else {
                                currentNote.setFolderId(folders.get(which - 1).getId());
                            }
                            saveNote(false);
                            Toast.makeText(requireContext(), "Moved to " + folderNames[which], Toast.LENGTH_SHORT).show();
                        })
                        .show();
            });
        });
    }

    private void insertFormatting(String prefix, String suffix) {
        int start = Math.max(binding.editTextContent.getSelectionStart(), 0);
        int end = Math.max(binding.editTextContent.getSelectionEnd(), 0);
        
        String text = binding.editTextContent.getText().toString();
        String selectedText = text.substring(start, end);
        String replacement = prefix + selectedText + suffix;
        
        binding.editTextContent.getText().replace(start, end, replacement);
        binding.editTextContent.setSelection(start + prefix.length(), end + prefix.length());
    }

    private void displayNote() {
        binding.editTextTitle.setText(currentNote.getTitle());
        binding.editTextContent.setText(currentNote.getContent());
        updateDateText();
        updatePinIcon();
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        binding.textViewDate.setText(getString(R.string.edited_at, sdf.format(new Date(currentNote.getUpdatedAt()))));
    }

    private void updatePinIcon() {
        MenuItem pinItem = binding.toolbar.getMenu().findItem(R.id.action_pin);
        if (pinItem != null) {
            pinItem.getIcon().setTint(currentNote.isPinned() ? Color.YELLOW : Color.WHITE);
            pinItem.setTitle(currentNote.isPinned() ? getString(R.string.unpin) : getString(R.string.pin));
        }
    }

    private void togglePin() {
        currentNote.setPinned(!currentNote.isPinned());
        updatePinIcon();
        saveNote(false);
    }

    private void saveNote(boolean showToast) {
        String title = binding.editTextTitle.getText().toString().trim();
        String content = binding.editTextContent.getText().toString();

        if (title.isEmpty() && content.isEmpty() && noteId == -1) return;

        currentNote.setTitle(title.isEmpty() ? getString(R.string.untitled_note) : title);
        currentNote.setContent(content);
        currentNote.setUpdatedAt(System.currentTimeMillis());

        if (noteId == -1) {
            viewModel.insertNote(currentNote, id -> {
                noteId = id;
                currentNote.setId(id);
            });
        } else {
            viewModel.updateNote(currentNote);
        }

        if (showToast) {
            Toast.makeText(requireContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
        }
        updateDateText();
    }

    private void shareNote() {
        String shareBody = currentNote.getTitle() + "\n\n" + currentNote.getContent();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, currentNote.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_note_via)));
    }

    private void deleteNote() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_note)
                .setMessage(R.string.delete_note_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (noteId != -1) {
                        viewModel.deleteNote(currentNote);
                    }
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void exportToPdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setFakeBoldText(true);

        int y = 50;
        canvas.drawText(currentNote.getTitle(), 50, y, paint);
        
        y += 40;
        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        
        String content = currentNote.getContent();
        String[] lines = content.split("\n");
        for (String line : lines) {
            canvas.drawText(line, 50, y, paint);
            y += 20;
            if (y > 800) {
                document.finishPage(page);
                page = document.startPage(new PdfDocument.PageInfo.Builder(595, 842, 2).create());
                canvas = page.getCanvas();
                y = 50;
            }
        }

        document.finishPage(page);

        File downloadsDir = new File(requireContext().getExternalFilesDir(null), "Notes");
        if (!downloadsDir.exists()) downloadsDir.mkdirs();
        
        File file = new File(downloadsDir, currentNote.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_") + ".pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(requireContext(), getString(R.string.exported_to, file.getAbsolutePath()), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), R.string.failed_export_pdf, Toast.LENGTH_SHORT).show();
        }
        document.close();
    }

    @Override
    public void onDestroyView() {
        autosaveHandler.removeCallbacks(autosaveRunnable);
        super.onDestroyView();
        binding = null;
    }
}
