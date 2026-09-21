package com.example.devpilotai.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.AdminLogRepository;
import com.example.devpilotai.data.UserRepository;
import com.example.devpilotai.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserManagementViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final AdminLogRepository adminLogRepository;
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private final MutableLiveData<List<User>> filteredUsers = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public UserManagementViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        adminLogRepository = new AdminLogRepository();
        loadUsers();
    }

    public void loadUsers() {
        loading.setValue(true);
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers.postValue(users);
                filteredUsers.postValue(users);
                loading.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    public void filterUsers(String query) {
        List<User> currentAll = allUsers.getValue();
        if (currentAll == null) return;
        
        if (query == null || query.isEmpty()) {
            filteredUsers.setValue(currentAll);
        } else {
            String lowerQuery = query.toLowerCase();
            List<User> filtered = currentAll.stream()
                .filter(u -> (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerQuery)) ||
                             (u.getDisplayName() != null && u.getDisplayName().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
            filteredUsers.setValue(filtered);
        }
    }

    public void updateUserStatus(User user, String newStatus) {
        loading.setValue(true);
        userRepository.updateUserStatus(user.getUid(), newStatus, new UserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                adminLogRepository.logAction("USER_STATUS_CHANGE", user.getEmail(), "Changed status to " + newStatus);
                loadUsers(); // Refresh
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    public void deleteUser(User user) {
        loading.setValue(true);
        userRepository.deleteUser(user.getUid(), new UserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                adminLogRepository.logAction("USER_DELETION", user.getEmail(), "Permanently deleted user");
                loadUsers(); // Refresh
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    public LiveData<List<User>> getFilteredUsers() { return filteredUsers; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }
}
