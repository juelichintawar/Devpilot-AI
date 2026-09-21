package com.example.devpilotai.ui.dashboard;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.data.RecentActivity;
import com.example.devpilotai.databinding.ItemRecentActivityBinding;

/**
 * Optimized adapter for Recent Activity list using ListAdapter.
 * Employs DiffUtil for efficient UI updates.
 */
public class RecentActivityAdapter extends ListAdapter<RecentActivity, RecentActivityAdapter.ViewHolder> {

    public RecentActivityAdapter() {
        super(new RecentActivityDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentActivityBinding binding = ItemRecentActivityBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecentActivityBinding binding;

        public ViewHolder(ItemRecentActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RecentActivity activity) {
            binding.tvActivityTitle.setText(activity.getTitle());
            binding.tvActivitySubtitle.setText(activity.getSubtitle());
            binding.tvActivityTime.setText(activity.getTime());
            binding.ivActivityIcon.setImageResource(activity.getIconRes());
            binding.ivActivityIcon.setImageTintList(ColorStateList.valueOf(activity.getIconColor()));
            binding.cvIconBg.setCardBackgroundColor(activity.getIconBackground());
        }
    }

    static class RecentActivityDiffCallback extends DiffUtil.ItemCallback<RecentActivity> {
        @Override
        public boolean areItemsTheSame(@NonNull RecentActivity oldItem, @NonNull RecentActivity newItem) {
            // Using title and time as a unique identifier for this demo model
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getTime().equals(newItem.getTime());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecentActivity oldItem, @NonNull RecentActivity newItem) {
            return oldItem.equals(newItem);
        }
    }
}
