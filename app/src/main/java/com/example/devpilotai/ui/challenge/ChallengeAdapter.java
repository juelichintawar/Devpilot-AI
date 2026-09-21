package com.example.devpilotai.ui.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devpilotai.R;
import com.example.devpilotai.data.model.Challenge;
import com.example.devpilotai.databinding.ItemChallengeBinding;

public class ChallengeAdapter extends ListAdapter<Challenge, ChallengeAdapter.ChallengeViewHolder> {

    private final OnChallengeClickListener listener;

    public interface OnChallengeClickListener {
        void onChallengeClick(Challenge challenge);
    }

    public ChallengeAdapter(OnChallengeClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Challenge> DIFF_CALLBACK = new DiffUtil.ItemCallback<Challenge>() {
        @Override
        public boolean areItemsTheSame(@NonNull Challenge oldItem, @NonNull Challenge newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Challenge oldItem, @NonNull Challenge newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDifficulty().equals(newItem.getDifficulty());
        }
    };

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChallengeBinding binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChallengeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        private final ItemChallengeBinding binding;

        public ChallengeViewHolder(ItemChallengeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Challenge challenge, OnChallengeClickListener listener) {
            binding.tvChallengeTitle.setText(challenge.getTitle());
            binding.tvChallengeDesc.setText(challenge.getDescription());
            binding.tvChallengeCategory.setText(challenge.getCategory().toUpperCase());
            binding.tvChallengeDifficulty.setText(challenge.getDifficulty().toUpperCase());
            binding.tvChallengeTime.setText(challenge.getTimeLimit() / 60 + " mins");

            // Set difficulty color
            int colorRes;
            switch (challenge.getDifficulty().toLowerCase()) {
                case "easy":
                    colorRes = R.color.accent_green;
                    break;
                case "medium":
                    colorRes = R.color.accent_orange;
                    break;
                case "hard":
                    colorRes = R.color.error;
                    break;
                default:
                    colorRes = R.color.text_secondary;
            }
            binding.tvChallengeDifficulty.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), colorRes));

            binding.btnStart.setOnClickListener(v -> listener.onChallengeClick(challenge));
            itemView.setOnClickListener(v -> listener.onChallengeClick(challenge));
        }
    }
}
