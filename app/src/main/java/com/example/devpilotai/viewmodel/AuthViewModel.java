package com.example.devpilotai.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.devpilotai.data.AuthRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final LiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> resetPasswordEmailSent = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
        userLiveData = authRepository.getUserLiveData();
    }

    public void login(String email, String password) {
        loadingLiveData.setValue(true);
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                loadingLiveData.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue(message);
            }
        });
    }

    public void register(String email, String password) {
        loadingLiveData.setValue(true);
        authRepository.register(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                loadingLiveData.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue(message);
            }
        });
    }

    public void signInWithGoogle(AuthCredential credential) {
        loadingLiveData.setValue(true);
        authRepository.firebaseSignInWithGoogle(credential, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                loadingLiveData.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue(message);
            }
        });
    }

    public void updateProfile(String displayName, String photoUrl) {
        loadingLiveData.setValue(true);
        authRepository.updateProfile(displayName, photoUrl, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                loadingLiveData.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue(message);
            }
        });
    }

    public void resetPassword(String email) {
        loadingLiveData.setValue(true);
        authRepository.resetPassword(email, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                loadingLiveData.postValue(false);
                resetPasswordEmailSent.postValue(true);
            }

            @Override
            public void onFailure(String message) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue(message);
            }
        });
    }

    public void logOut() {
        authRepository.logOut();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<Boolean> getResetPasswordEmailSent() {
        return resetPasswordEmailSent;
    }
}