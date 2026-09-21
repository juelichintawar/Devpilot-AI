package com.example.devpilotai.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.data.QuickAction;
import com.example.devpilotai.databinding.ItemQuickActionBinding;

/**
 * Optimized adapter for Quick Actions grid using ListAdapter.
 */
public class QuickActionAdapter extends ListAdapter<QuickAction, QuickActionAdapter.ViewHolder> {

    private final OnActionClickListener listener;

    public interface OnActionClickListener {
        void onActionClick(QuickAction action);
    }

    public QuickActionAdapter(OnActionClickListener listener) {
        super(new QuickActionDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuickActionBinding binding = ItemQuickActionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemQuickActionBinding binding;

        public ViewHolder(ItemQuickActionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(QuickAction action, OnActionClickListener listener) {
            binding.tvActionTitle.setText(action.getTitle());
            binding.ivActionIcon.setImageResource(action.getIconRes());
            
            int color = ContextCompat.getColor(itemView.getContext(), action.getBackgroundColor());
            binding.getRoot().setCardBackgroundColor(color);
            
            binding.getRoot().setOnClickListener(v -> listener.onActionClick(action));
        }
    }

    static class QuickActionDiffCallback extends DiffUtil.ItemCallback<QuickAction> {
        @Override
        public boolean areItemsTheSame(@NonNull QuickAction oldItem, @NonNull QuickAction newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull QuickAction oldItem, @NonNull QuickAction newItem) {
            return oldItem.equals(newItem);
        }
    }
}
