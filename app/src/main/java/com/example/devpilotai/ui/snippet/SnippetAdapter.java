package com.example.devpilotai.ui.snippet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.Snippet;
import com.example.devpilotai.databinding.ItemSnippetBinding;
import com.google.android.material.chip.Chip;

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
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.isFavorite() == newItem.isFavorite() &&
                    (oldItem.getTags() != null ? oldItem.getTags().equals(newItem.getTags()) : newItem.getTags() == null);
        }
    };

    @NonNull
    @Override
    public SnippetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSnippetBinding binding = ItemSnippetBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SnippetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SnippetViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class SnippetViewHolder extends RecyclerView.ViewHolder {
        private final ItemSnippetBinding binding;

        public SnippetViewHolder(ItemSnippetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSnippetClick(getItem(position));
                }
            });

            binding.btnFavorite.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(getItem(position));
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position));
                }
            });

            binding.btnCopy.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Snippet snippet = getItem(position);
                    copyToClipboard(v.getContext(), snippet.getContent());
                }
            });
        }

        public void bind(Snippet snippet) {
            binding.textSnippetTitle.setText(snippet.getTitle());
            binding.textSnippetContent.setText(snippet.getContent());
            binding.textCategory.setText(snippet.getCategory());
            
            if (snippet.isFavorite()) {
                binding.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                binding.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            }

            binding.tagGroup.removeAllViews();
            if (snippet.getTags() != null && !snippet.getTags().isEmpty()) {
                String[] tags = snippet.getTags().split(",");
                for (String tag : tags) {
                    String trimmedTag = tag.trim();
                    if (!trimmedTag.isEmpty()) {
                        Chip chip = new Chip(itemView.getContext());
                        chip.setText(trimmedTag);
                        chip.setCheckable(false);
                        chip.setClickable(false);
                        binding.tagGroup.addView(chip);
                    }
                }
            }
        }

        private void copyToClipboard(Context context, String text) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Snippet Content", text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Snippet copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
