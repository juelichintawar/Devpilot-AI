
        package com.example.devpilotai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply Theme from Preferences before onCreate
        applyTheme();

        super.onCreate(savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Configure Firebase App Check for local development
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply Window Insets for Edge-to-Edge, including keyboard (IME)
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    Insets ime = insets.getInsets(
                            WindowInsetsCompat.Type.ime()
                    );

                    // Use the maximum bottom padding between
                    // navigation bars and the keyboard
                    int bottomPadding = Math.max(
                            bars.bottom,
                            ime.bottom
                    );

                    v.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            bottomPadding
                    );

                    return insets;
                }
        );

        // Setup Navigation
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {

            NavController navController =
                    navHostFragment.getNavController();

            if (getSupportActionBar() != null) {
                NavigationUI.setupActionBarWithNavController(
                        this,
                        navController
                );
            }
        }
    }

    private void applyTheme() {

        SharedPreferences prefs =
                getSharedPreferences(
                        "settings",
                        Context.MODE_PRIVATE
                );

        int themeMode =
                prefs.getInt("theme_mode", 0);

        // 0 = System
        // 1 = Light
        // 2 = Dark
        switch (themeMode) {

            case 1:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );
                break;

            case 2:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );
                break;

            default:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                );
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        return (
                navHostFragment != null
                        && navHostFragment
                        .getNavController()
                        .navigateUp()
        ) || super.onSupportNavigateUp();
    }
}