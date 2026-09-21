package com.example.devpilotai.ui.search;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.SearchResult;
import com.example.devpilotai.databinding.ItemSearchResultBinding;

import java.util.Locale;

/**
 * Optimized adapter for Search results using ListAdapter and DiffUtil.
 * Provides text highlighting for search queries.
 */
public class SearchAdapter extends ListAdapter<SearchResult, SearchAdapter.ViewHolder> {

    private String currentQuery = "";
    private final OnResultClickListener listener;
    private int highlightColor = -1;

    public interface OnResultClickListener {
        void onResultClick(SearchResult result);
    }

    public SearchAdapter(OnResultClickListener listener) {
        super(new SearchDiffCallback());
        this.listener = listener;
    }

    public void setQuery(String query) {
        this.currentQuery = query;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (highlightColor == -1) {
            TypedValue typedValue = new TypedValue();
            Context context = parent.getContext();
            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
            highlightColor = typedValue.data;
        }
        ItemSearchResultBinding binding = ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchResultBinding binding;

        ViewHolder(ItemSearchResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SearchResult result) {
            binding.tvTitle.setText(getHighlightedText(result.getTitle(), currentQuery));
            binding.tvContent.setText(getHighlightedText(result.getContent(), currentQuery));
            binding.tvTypeLabel.setText(result.getType().name());

            int iconRes;
            switch (result.getType()) {
                case SNIPPET: iconRes = android.R.drawable.ic_menu_edit; break;
                case NOTE: iconRes = android.R.drawable.ic_menu_sort_alphabetically; break;
                case CHAT: iconRes = android.R.drawable.ic_menu_send; break;
                case REPOSITORY: iconRes = android.R.drawable.ic_menu_share; break;
                default: iconRes = android.R.drawable.ic_menu_search;
            }
            binding.ivTypeIcon.setImageResource(iconRes);

            itemView.setOnClickListener(v -> listener.onResultClick(result));
        }

        private CharSequence getHighlightedText(String fullText, String query) {
            if (fullText == null) return "";
            if (query == null || query.isEmpty()) return fullText;

            SpannableString spannable = new SpannableString(fullText);
            String lowerFullText = fullText.toLowerCase(Locale.getDefault());
            String lowerQuery = query.toLowerCase(Locale.getDefault());

            int start = lowerFullText.indexOf(lowerQuery);
            while (start >= 0) {
                int end = start + lowerQuery.length();
                spannable.setSpan(new ForegroundColorSpan(highlightColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = lowerFullText.indexOf(lowerQuery, end);
            }
            return spannable;
        }
    }

    static class SearchDiffCallback extends DiffUtil.ItemCallback<SearchResult> {
        @Override
        public boolean areItemsTheSame(@NonNull SearchResult oldItem, @NonNull SearchResult newItem) {
            return oldItem.getId().equals(newItem.getId()) && oldItem.getType() == newItem.getType();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchResult oldItem, @NonNull SearchResult newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getContent().equals(newItem.getContent());
        }
    }
}
