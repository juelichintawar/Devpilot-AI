package com.example.devpilotai.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devpilotai.R;
import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.ItemUserBinding;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users = new ArrayList<>();
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onToggleStatus(User user);
        void onDeleteUser(User user);
        void onUserClick(User user);
    }

    public UserAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.tvUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "No Name");
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvUserStatus.setText(user.getStatus());
            
            boolean isActive = "Active".equalsIgnoreCase(user.getStatus());
            binding.tvUserStatus.setBackgroundResource(isActive ? R.drawable.bg_status_active : R.drawable.bg_status_deactivated);
            binding.btnToggleStatus.setText(isActive ? "Deactivate" : "Activate");
            binding.btnToggleStatus.setTextColor(itemView.getContext().getColor(isActive ? R.color.accent_orange : R.color.accent_green));

            binding.btnToggleStatus.setOnClickListener(v -> listener.onToggleStatus(user));
            binding.btnDeleteUser.setOnClickListener(v -> listener.onDeleteUser(user));
            itemView.setOnClickListener(v -> listener.onUserClick(user));
        }
    }
}
