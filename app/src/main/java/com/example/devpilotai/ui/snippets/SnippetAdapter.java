package com.example.devpilotai.ui.snippets;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.Snippet;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class SnippetAdapter extends ListAdapter<Snippet, SnippetAdapter.SnippetViewHolder> {

    private final OnSnippetClickListener listener;

    public interface OnSnippetClickListener {
        void onSnippetClick(Snippet snippet);
        void onFavoriteClick(Snippet snippet);
        void onDeleteClick(Snippet snippet);
    }

    public SnippetAdapter(OnSnippetClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Snippet> DIFF_CALLBACK = new DiffUtil.ItemCallback<Snippet>() {
        @Override
        public boolean areItemsTheSame(@NonNull Snippet oldItem, @NonNull Snippet newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Snippet oldItem, @NonNull Snippet newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.isFavorite() == newItem.isFavorite() &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getTags().equals(newItem.getTags());
        }
    };

    @NonNull
    @Override
    public SnippetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_snippet, parent, false);
        return new SnippetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SnippetViewHolder holder, int position) {
        Snippet currentSnippet = getItem(position);
        holder.bind(currentSnippet, listener);
    }

    class SnippetViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewContent;
        private final TextView textViewCategory;
        private final ImageButton btnFavorite;
        private final ImageButton btnCopy;
        private final ImageButton btnDelete;
        private final ChipGroup tagGroup;

        public SnippetViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textSnippetTitle);
            textViewContent = itemView.findViewById(R.id.textSnippetContent);
            textViewCategory = itemView.findViewById(R.id.textCategory);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnCopy = itemView.findViewById(R.id.btnCopy);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tagGroup = itemView.findViewById(R.id.tagGroup);
        }

        public void bind(Snippet snippet, OnSnippetClickListener listener) {
            textViewTitle.setText(snippet.getTitle());
            textViewContent.setText(snippet.getContent());
            textViewCategory.setText(snippet.getCategory());
            
            if (snippet.isFavorite()) {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            }

            // Display tags
            tagGroup.removeAllViews();
            if (snippet.getTags() != null && !snippet.getTags().isEmpty()) {
                String[] tags = snippet.getTags().split(",");
                for (String tag : tags) {
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(tag.trim());
                    chip.setChipMinHeight(24f);
                    chip.setTextSize(10f);
                    tagGroup.addView(chip);
                }
            }

            itemView.setOnClickListener(v -> listener.onSnippetClick(snippet));
            btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(snippet));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(snippet));
            
            btnCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Snippet Content", snippet.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(itemView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
