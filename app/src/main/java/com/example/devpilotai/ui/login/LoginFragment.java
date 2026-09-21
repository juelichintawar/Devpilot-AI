package com.example.devpilotai.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.databinding.FragmentLoginBinding;
import com.example.devpilotai.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Use Activity scope to share Auth state across all fragments
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        setupGoogleSignIn();
        checkRememberMe();
        observeViewModel();

        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        binding.tvRegister.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));
        binding.tvForgotPassword.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
        
        // Admin Portal navigation
        binding.tvAdminPortal.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_adminLoginFragment));
    }

    private void setupGoogleSignIn() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        } catch (Exception e) {
            // This might happen if default_web_client_id is missing or invalid
            Toast.makeText(getContext(), "Google Sign-In configuration error", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRememberMe() {
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        if (isRemembered) {
            binding.etEmail.setText(sharedPreferences.getString("email", ""));
            binding.etPassword.setText(sharedPreferences.getString("password", ""));
            binding.cbRememberMe.setChecked(true);
        }
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (!validateEmail(email) || !validatePassword(password)) return;

        if (binding.cbRememberMe.isChecked()) {
            sharedPreferences.edit()
                    .putBoolean("remember", true)
                    .putString("email", email)
                    .putString("password", password)
                    .apply();
        } else {
            sharedPreferences.edit().clear().apply();
        }

        authViewModel.login(email, password);
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.invalid_email));
            return false;
        }
        binding.tilEmail.setError(null);
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.invalid_password));
            return false;
        }
        binding.tilPassword.setError(null);
        return true;
    }

    private void signInWithGoogle() {
        if (googleSignInClient == null) {
            Toast.makeText(getContext(), "Google Sign-In is not configured", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                String errorMsg = "Google sign in failed: " + e.getStatusCode();
                if (e.getStatusCode() == 10) {
                    errorMsg = "Sign-in failed. Please check your SHA-1 fingerprint in Firebase Console.";
                }
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        authViewModel.signInWithGoogle(credential);
    }

    private void observeViewModel() {
        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                NavController navController = Navigation.findNavController(requireView());
                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.loginFragment) {
                    navController.navigate(R.id.action_loginFragment_to_dashboardFragment);
                }
            }
        });

        authViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnGoogleSignIn.setEnabled(!isLoading);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
